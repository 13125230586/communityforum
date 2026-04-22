#!/usr/bin/env python3
# -*- coding: utf-8 -*-

from __future__ import print_function

import argparse
import base64
import http.cookiejar
import json
import mimetypes
import os
import re
import subprocess
import sys
import tempfile
import time
import urllib.error
import urllib.parse
import urllib.request
import uuid
from collections import OrderedDict
from datetime import datetime
from pathlib import Path


SUCCESS_CODE = 0
NORMAL_USER_STATUS = 0
DISABLED_USER_STATUS = 1
AUDIT_STATUS_PASS = 1
FLAG_YES = 1
FLAG_NO = 0
PROCESS_STATUS_DONE = 1
DEFAULT_TIMEOUT = 20
TEST_IMAGE_BASE64 = (
    "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8"
    "/w8AAgMBAp6X7k8AAAAASUVORK5CYII="
)


class TestFailure(Exception):
    pass


class StepLogger(object):

    def __init__(self):
        self.results = []

    def pass_step(self, stage_name, step_name, detail):
        self.results.append({
            "stage": stage_name,
            "step": step_name,
            "success": True,
            "detail": detail,
        })
        print("[PASS] {} | {} | {}".format(stage_name, step_name, detail))

    def fail_step(self, stage_name, step_name, detail):
        self.results.append({
            "stage": stage_name,
            "step": step_name,
            "success": False,
            "detail": detail,
        })
        print("[FAIL] {} | {} | {}".format(stage_name, step_name, detail))

    def build_summary(self):
        total_count = len(self.results)
        pass_count = len([item for item in self.results if item["success"]])
        fail_count = total_count - pass_count
        return {
            "totalCount": total_count,
            "passCount": pass_count,
            "failCount": fail_count,
            "results": self.results,
        }


class HttpClient(object):

    def __init__(self, base_url, timeout=DEFAULT_TIMEOUT):
        self.base_url = base_url.rstrip("/")
        self.timeout = timeout
        self.cookie_jar = http.cookiejar.CookieJar()
        self.opener = urllib.request.build_opener(urllib.request.HTTPCookieProcessor(self.cookie_jar))

    def get(self, path, params=None):
        query = ""
        if params:
            query = "?" + urllib.parse.urlencode(params)
        return self._request("GET", path + query, headers={})

    def post_json(self, path, payload):
        body = json.dumps(payload).encode("utf-8")
        headers = {"Content-Type": "application/json"}
        return self._request("POST", path, body=body, headers=headers)

    def post_multipart(self, path, files):
        boundary = "----CodexBoundary{}".format(uuid.uuid4().hex)
        body = self._build_multipart_body(boundary, files)
        headers = {"Content-Type": "multipart/form-data; boundary={}".format(boundary)}
        return self._request("POST", path, body=body, headers=headers)

    def _build_multipart_body(self, boundary, files):
        body = bytearray()
        for field_name, file_info in files.items():
            filename = file_info["filename"]
            content = file_info["content"]
            content_type = file_info.get("contentType") or mimetypes.guess_type(filename)[0] or "application/octet-stream"
            body.extend(("--{}\r\n".format(boundary)).encode("utf-8"))
            disposition = 'Content-Disposition: form-data; name="{}"; filename="{}"\r\n'.format(field_name, filename)
            body.extend(disposition.encode("utf-8"))
            body.extend(("Content-Type: {}\r\n\r\n".format(content_type)).encode("utf-8"))
            body.extend(content)
            body.extend(b"\r\n")
        body.extend(("--{}--\r\n".format(boundary)).encode("utf-8"))
        return bytes(body)

    def _request(self, method, path, body=None, headers=None):
        url = self.base_url + path
        request = urllib.request.Request(url=url, data=body, headers=headers or {}, method=method)
        try:
            response = self.opener.open(request, timeout=self.timeout)
            raw_data = response.read().decode("utf-8")
            return self._parse_json(raw_data, response.getcode(), url)
        except urllib.error.HTTPError as error:
            raw_data = error.read().decode("utf-8")
            return self._parse_json(raw_data, error.code, url)
        except Exception as error:
            raise TestFailure("请求失败 url:{} error:{}".format(url, error))

    def _parse_json(self, raw_data, http_status, url):
        try:
            data = json.loads(raw_data)
        except Exception:
            raise TestFailure("响应非JSON url:{} httpStatus:{} body:{}".format(url, http_status, raw_data))
        data["httpStatus"] = http_status
        data["requestUrl"] = url
        return data


class ForumApiTester(object):

    def __init__(self, base_url, project_root):
        self.base_url = base_url.rstrip("/")
        self.project_root = Path(project_root)
        self.resource_root = self.project_root / "src" / "main" / "resources"
        self.logger = StepLogger()
        self.normal_client = HttpClient(self.base_url)
        self.admin_client = HttpClient(self.base_url)
        self.context = {}
        self.unique_suffix = datetime.now().strftime("%Y%m%d%H%M%S")

    def run(self):
        self.run_stage_one()
        self.run_stage_two()
        self.run_stage_three()
        self.run_stage_four()
        self.run_stage_five()
        return self.logger.build_summary()

    def run_stage_one(self):
        stage_name = "第一阶段 基础检查"
        self.exec_step(stage_name, "健康检查", self.step_health)
        self.exec_step(stage_name, "注册普通用户", self.step_register_normal_user)
        self.exec_step(stage_name, "登录普通用户", self.step_login_normal_user)
        self.exec_step(stage_name, "获取普通用户登录态", self.step_get_normal_login_user)

    def run_stage_two(self):
        stage_name = "第二阶段 普通用户链路"
        self.exec_step(stage_name, "查询分类列表", self.step_list_category)
        self.exec_step(stage_name, "查询板块列表", self.step_list_board)
        self.exec_step(stage_name, "查询板块详情", self.step_get_board)
        self.exec_step(stage_name, "查询标签列表", self.step_list_tag)
        self.exec_step(stage_name, "上传帖子封面", self.step_upload_post_cover)
        self.exec_step(stage_name, "上传头像", self.step_upload_avatar)
        self.exec_step(stage_name, "上传正文图片", self.step_upload_content_image)
        self.exec_step(stage_name, "发布主帖子", self.step_add_primary_post)
        self.exec_step(stage_name, "发布待删除帖子", self.step_add_delete_post)
        self.exec_step(stage_name, "查询帖子分页", self.step_list_post_page)
        self.exec_step(stage_name, "查询帖子详情", self.step_get_post_detail)
        self.exec_step(stage_name, "更新主帖子", self.step_update_primary_post)
        self.exec_step(stage_name, "新增主评论", self.step_add_primary_comment)
        self.exec_step(stage_name, "新增待删除评论", self.step_add_delete_comment)
        self.exec_step(stage_name, "查询评论分页", self.step_list_comment_page)
        self.exec_step(stage_name, "更新个人资料", self.step_update_profile)

    def run_stage_three(self):
        stage_name = "第三阶段 内容互动链路"
        self.exec_step(stage_name, "帖子点赞", self.step_toggle_post_like)
        self.exec_step(stage_name, "帖子收藏", self.step_toggle_post_collect)
        self.exec_step(stage_name, "评论点赞", self.step_toggle_comment_like)
        self.exec_step(stage_name, "新增举报", self.step_add_report)
        self.exec_step(stage_name, "删除本人文件", self.step_delete_own_file)
        self.exec_step(stage_name, "删除待删除评论", self.step_delete_comment)
        self.exec_step(stage_name, "删除待删除帖子", self.step_delete_post)

    def run_stage_four(self):
        stage_name = "第四阶段 管理员后台链路"
        self.exec_step(stage_name, "普通用户退出登录", self.step_logout_normal_user)
        self.exec_step(stage_name, "注册管理员测试用户", self.step_register_admin_user)
        self.exec_step(stage_name, "提升管理员权限", self.step_promote_admin_user)
        self.exec_step(stage_name, "登录管理员", self.step_login_admin_user)
        self.exec_step(stage_name, "获取管理员登录态", self.step_get_admin_login_user)
        self.exec_step(stage_name, "新增分类", self.step_admin_add_category)
        self.exec_step(stage_name, "更新分类", self.step_admin_update_category)
        self.exec_step(stage_name, "新增板块", self.step_admin_add_board)
        self.exec_step(stage_name, "更新板块", self.step_admin_update_board)
        self.exec_step(stage_name, "删除板块", self.step_admin_delete_board)
        self.exec_step(stage_name, "删除分类", self.step_admin_delete_category)
        self.exec_step(stage_name, "新增标签", self.step_admin_add_tag)
        self.exec_step(stage_name, "标签分页", self.step_admin_list_tag_page)
        self.exec_step(stage_name, "更新标签", self.step_admin_update_tag)
        self.exec_step(stage_name, "删除标签", self.step_admin_delete_tag)
        self.exec_step(stage_name, "后台帖子分页", self.step_admin_list_post_page)
        self.exec_step(stage_name, "后台审核帖子", self.step_admin_audit_post)
        self.exec_step(stage_name, "后台置顶帖子", self.step_admin_update_top)
        self.exec_step(stage_name, "后台精华帖子", self.step_admin_update_essence)
        self.exec_step(stage_name, "后台评论分页", self.step_admin_list_comment_page)
        self.exec_step(stage_name, "后台审核评论", self.step_admin_audit_comment)
        self.exec_step(stage_name, "后台举报分页", self.step_admin_list_report_page)
        self.exec_step(stage_name, "后台处理举报", self.step_admin_process_report)
        self.exec_step(stage_name, "后台用户分页", self.step_admin_list_user_page)
        self.exec_step(stage_name, "后台更新用户状态", self.step_admin_update_user_status)
        self.exec_step(stage_name, "后台更新用户禁言", self.step_admin_update_user_mute)
        self.exec_step(stage_name, "后台查询系统配置列表", self.step_admin_list_system_config)
        self.exec_step(stage_name, "后台查询系统配置分页", self.step_admin_list_system_config_page)
        self.exec_step(stage_name, "后台更新系统配置", self.step_admin_update_system_config)
        self.exec_step(stage_name, "后台文件分页", self.step_admin_list_file_page)
        self.exec_step(stage_name, "后台删除文件", self.step_admin_delete_file)
        self.exec_step(stage_name, "后台操作日志分页", self.step_admin_list_operation_log)
        self.exec_step(stage_name, "后台统计概览", self.step_admin_statistics_overview)
        self.exec_step(stage_name, "后台统计趋势", self.step_admin_statistics_trend)
        self.exec_step(stage_name, "后台板块排行", self.step_admin_statistics_board_rank)

    def run_stage_five(self):
        stage_name = "第五阶段 回归验证"
        self.exec_step(stage_name, "回归查询帖子分页", self.step_list_post_page)
        self.exec_step(stage_name, "回归查询帖子详情", self.step_get_post_detail)
        self.exec_step(stage_name, "回归查询评论分页", self.step_list_comment_page)
        self.exec_step(stage_name, "回归查询文件分页", self.step_admin_list_file_page)
        self.exec_step(stage_name, "回归查询操作日志", self.step_admin_list_operation_log)
        self.exec_step(stage_name, "回归查询统计概览", self.step_admin_statistics_overview)

    def exec_step(self, stage_name, step_name, func):
        try:
            detail = func()
            self.logger.pass_step(stage_name, step_name, detail)
        except Exception as error:
            self.logger.fail_step(stage_name, step_name, str(error))
            raise

    def step_health(self):
        response = self.normal_client.get("/health")
        self.expect_code(response, "健康检查")
        return "服务可用"

    def step_register_normal_user(self):
        user_account = "apitest_user_{}".format(self.unique_suffix)
        password = "12345678"
        payload = {
            "userAccount": user_account,
            "userPassword": password,
            "checkPassword": password,
        }
        response = self.normal_client.post_json("/user/register", payload)
        self.expect_code(response, "注册普通用户")
        self.context["normalUserAccount"] = user_account
        self.context["normalUserPassword"] = password
        self.context["normalUserId"] = response.get("data")
        return "uid:{} account:{}".format(response.get("data"), user_account)

    def step_login_normal_user(self):
        payload = {
            "userAccount": self.context["normalUserAccount"],
            "userPassword": self.context["normalUserPassword"],
        }
        response = self.normal_client.post_json("/user/login", payload)
        self.expect_code(response, "登录普通用户")
        login_user = response.get("data") or {}
        self.context["normalLoginUser"] = login_user
        return "uid:{} role:{}".format(login_user.get("id"), login_user.get("userRole"))

    def step_get_normal_login_user(self):
        response = self.normal_client.get("/user/get/login")
        self.expect_code(response, "获取普通用户登录态")
        login_user = response.get("data") or {}
        self.context["normalLoginUser"] = login_user
        return "uid:{}".format(login_user.get("id"))

    def step_list_category(self):
        response = self.normal_client.get("/boardCategory/list")
        self.expect_code(response, "查询分类列表")
        category_list = response.get("data") or []
        self.assert_true(len(category_list) > 0, "分类列表为空")
        self.context["categoryId"] = category_list[0]["id"]
        return "categoryId:{} count:{}".format(self.context["categoryId"], len(category_list))

    def step_list_board(self):
        response = self.normal_client.get("/board/list")
        self.expect_code(response, "查询板块列表")
        board_list = response.get("data") or []
        self.assert_true(len(board_list) > 0, "板块列表为空")
        self.context["boardId"] = board_list[0]["id"]
        return "boardId:{} count:{}".format(self.context["boardId"], len(board_list))

    def step_get_board(self):
        response = self.normal_client.get("/board/get", {"id": self.context["boardId"]})
        self.expect_code(response, "查询板块详情")
        board_data = response.get("data") or {}
        self.assert_true(board_data.get("id") == self.context["boardId"], "板块ID不匹配")
        return "boardName:{}".format(board_data.get("boardName"))

    def step_list_tag(self):
        response = self.normal_client.get("/post/tag/list")
        self.expect_code(response, "查询标签列表")
        tag_list = response.get("data") or []
        self.assert_true(len(tag_list) > 0, "标签列表为空")
        tag_id_list = [item["id"] for item in tag_list[:2]]
        self.context["tagIdList"] = tag_id_list
        return "tagIds:{}".format(tag_id_list)

    def step_upload_post_cover(self):
        file_info = self.upload_test_file(self.normal_client, "/file/upload/postCover", "cover.png")
        self.context["postCoverFile"] = file_info
        self.context["postCoverUrl"] = file_info["fileUrl"]
        return "fileId:{}".format(file_info["fileId"])

    def step_upload_avatar(self):
        file_info = self.upload_test_file(self.normal_client, "/file/upload/avatar", "avatar.png")
        self.context["avatarFile"] = file_info
        return "fileId:{}".format(file_info["fileId"])

    def step_upload_content_image(self):
        file_info = self.upload_test_file(self.normal_client, "/file/upload/postContentImage", "content.png")
        self.context["contentImageFile"] = file_info
        return "fileId:{}".format(file_info["fileId"])

    def step_add_primary_post(self):
        payload = {
            "boardId": self.context["boardId"],
            "postTitle": "主帖子 {}".format(self.unique_suffix),
            "postSummary": "主帖子摘要 {}".format(self.unique_suffix),
            "coverImage": self.context["postCoverUrl"],
            "contentType": "markdown",
            "content": "# 主帖子\n\n这是自动化测试主帖子 {}".format(self.unique_suffix),
            "anonymousFlag": FLAG_NO,
            "commentAllowedFlag": FLAG_YES,
            "tagIdList": self.context["tagIdList"],
        }
        response = self.normal_client.post_json("/post/add", payload)
        self.expect_code(response, "发布主帖子")
        self.context["primaryPostId"] = response.get("data")
        return "postId:{}".format(self.context["primaryPostId"])

    def step_add_delete_post(self):
        payload = {
            "boardId": self.context["boardId"],
            "postTitle": "待删除帖子 {}".format(self.unique_suffix),
            "postSummary": "待删除帖子摘要 {}".format(self.unique_suffix),
            "coverImage": self.context["postCoverUrl"],
            "contentType": "markdown",
            "content": "# 待删除帖子\n\n用于删除接口校验 {}".format(self.unique_suffix),
            "anonymousFlag": FLAG_NO,
            "commentAllowedFlag": FLAG_YES,
            "tagIdList": self.context["tagIdList"],
        }
        response = self.normal_client.post_json("/post/add", payload)
        self.expect_code(response, "发布待删除帖子")
        self.context["deletePostId"] = response.get("data")
        return "postId:{}".format(self.context["deletePostId"])

    def step_list_post_page(self):
        response = self.normal_client.get("/post/list/page", {"current": 1, "pageSize": 10})
        self.expect_code(response, "查询帖子分页")
        page_data = response.get("data") or {}
        records = page_data.get("records") or []
        self.assert_true(len(records) > 0, "帖子分页为空")
        return "count:{}".format(len(records))

    def step_get_post_detail(self):
        response = self.normal_client.get("/post/get", {"id": self.context["primaryPostId"]})
        self.expect_code(response, "查询帖子详情")
        post_data = response.get("data") or {}
        self.assert_true(post_data.get("id") == self.context["primaryPostId"], "帖子详情ID不匹配")
        return "title:{}".format(post_data.get("postTitle"))

    def step_update_primary_post(self):
        payload = {
            "id": self.context["primaryPostId"],
            "boardId": self.context["boardId"],
            "postTitle": "主帖子已更新 {}".format(self.unique_suffix),
            "postSummary": "更新后的摘要 {}".format(self.unique_suffix),
            "coverImage": self.context["postCoverUrl"],
            "contentType": "markdown",
            "content": "# 更新后主帖子\n\n更新内容 {}".format(self.unique_suffix),
            "anonymousFlag": FLAG_NO,
            "commentAllowedFlag": FLAG_YES,
            "tagIdList": self.context["tagIdList"],
        }
        response = self.normal_client.post_json("/post/update", payload)
        self.expect_code(response, "更新主帖子")
        return "postId:{}".format(self.context["primaryPostId"])

    def step_add_primary_comment(self):
        payload = {
            "postId": self.context["primaryPostId"],
            "rootCommentId": 0,
            "parentCommentId": 0,
            "replyUserId": None,
            "contentType": "text",
            "content": "主评论 {}".format(self.unique_suffix),
            "anonymousFlag": FLAG_NO,
        }
        response = self.normal_client.post_json("/comment/add", payload)
        self.expect_code(response, "新增主评论")
        self.context["primaryCommentId"] = response.get("data")
        return "commentId:{}".format(self.context["primaryCommentId"])

    def step_add_delete_comment(self):
        payload = {
            "postId": self.context["primaryPostId"],
            "rootCommentId": 0,
            "parentCommentId": 0,
            "replyUserId": None,
            "contentType": "text",
            "content": "待删除评论 {}".format(self.unique_suffix),
            "anonymousFlag": FLAG_NO,
        }
        response = self.normal_client.post_json("/comment/add", payload)
        self.expect_code(response, "新增待删除评论")
        self.context["deleteCommentId"] = response.get("data")
        return "commentId:{}".format(self.context["deleteCommentId"])

    def step_list_comment_page(self):
        response = self.normal_client.get("/comment/list/page", {
            "current": 1,
            "pageSize": 10,
            "postId": self.context["primaryPostId"],
        })
        self.expect_code(response, "查询评论分页")
        page_data = response.get("data") or {}
        records = page_data.get("records") or []
        self.assert_true(len(records) > 0, "评论分页为空")
        return "count:{}".format(len(records))

    def step_update_profile(self):
        payload = {
            "userName": "普通用户{}".format(self.unique_suffix),
            "userAvatar": self.context["avatarFile"]["fileUrl"],
            "userProfile": "自动化测试普通用户 {}".format(self.unique_suffix),
            "phone": "1380000{}".format(self.unique_suffix[-4:]),
            "email": "apitest_{}@example.com".format(self.unique_suffix),
        }
        response = self.normal_client.post_json("/user/update/profile", payload)
        self.expect_code(response, "更新个人资料")
        return "email:{}".format(payload["email"])

    def step_toggle_post_like(self):
        response = self.normal_client.post_json("/post/like", {"bizId": self.context["primaryPostId"]})
        self.expect_code(response, "帖子点赞")
        return "postId:{}".format(self.context["primaryPostId"])

    def step_toggle_post_collect(self):
        response = self.normal_client.post_json("/post/collect", {"bizId": self.context["primaryPostId"]})
        self.expect_code(response, "帖子收藏")
        return "postId:{}".format(self.context["primaryPostId"])

    def step_toggle_comment_like(self):
        response = self.normal_client.post_json("/comment/like", {"bizId": self.context["primaryCommentId"]})
        self.expect_code(response, "评论点赞")
        return "commentId:{}".format(self.context["primaryCommentId"])

    def step_add_report(self):
        payload = {
            "bizType": "post",
            "bizId": self.context["primaryPostId"],
            "reportType": "spam",
            "reportReason": "自动化测试举报 {}".format(self.unique_suffix),
        }
        response = self.normal_client.post_json("/report/add", payload)
        self.expect_code(response, "新增举报")
        self.context["reportId"] = response.get("data")
        return "reportId:{}".format(self.context["reportId"])

    def step_delete_own_file(self):
        file_id = self.context["avatarFile"]["fileId"]
        response = self.normal_client.post_json("/file/delete", {"id": file_id})
        self.expect_code(response, "删除本人文件")
        return "fileId:{}".format(file_id)

    def step_delete_comment(self):
        response = self.normal_client.post_json("/comment/delete", {"id": self.context["deleteCommentId"]})
        self.expect_code(response, "删除待删除评论")
        return "commentId:{}".format(self.context["deleteCommentId"])

    def step_delete_post(self):
        response = self.normal_client.post_json("/post/delete", {"id": self.context["deletePostId"]})
        self.expect_code(response, "删除待删除帖子")
        return "postId:{}".format(self.context["deletePostId"])

    def step_logout_normal_user(self):
        response = self.normal_client.post_json("/user/logout", {})
        self.expect_code(response, "普通用户退出登录")
        return "普通用户已退出"

    def step_register_admin_user(self):
        admin_account = "apitest_admin_{}".format(self.unique_suffix)
        password = "12345678"
        payload = {
            "userAccount": admin_account,
            "userPassword": password,
            "checkPassword": password,
        }
        response = self.admin_client.post_json("/user/register", payload)
        self.expect_code(response, "注册管理员测试用户")
        self.context["adminUserAccount"] = admin_account
        self.context["adminUserPassword"] = password
        self.context["adminUserId"] = response.get("data")
        return "uid:{} account:{}".format(response.get("data"), admin_account)

    def step_promote_admin_user(self):
        datasource = self.parse_datasource_config()
        self.promote_user_to_admin(datasource, self.context["adminUserAccount"])
        return "account:{} 已提升为admin".format(self.context["adminUserAccount"])

    def step_login_admin_user(self):
        payload = {
            "userAccount": self.context["adminUserAccount"],
            "userPassword": self.context["adminUserPassword"],
        }
        response = self.admin_client.post_json("/user/login", payload)
        self.expect_code(response, "登录管理员")
        login_user = response.get("data") or {}
        self.assert_true(login_user.get("userRole") == "admin", "管理员登录后角色不正确")
        self.context["adminLoginUser"] = login_user
        return "uid:{} role:{}".format(login_user.get("id"), login_user.get("userRole"))

    def step_get_admin_login_user(self):
        response = self.admin_client.get("/user/get/login")
        self.expect_code(response, "获取管理员登录态")
        login_user = response.get("data") or {}
        self.assert_true(login_user.get("userRole") == "admin", "管理员登录态角色不正确")
        return "uid:{}".format(login_user.get("id"))

    def step_admin_add_category(self):
        payload = {
            "categoryName": "自动分类 {}".format(self.unique_suffix),
            "categoryIcon": "",
            "sortOrder": 88,
            "categoryStatus": 0,
        }
        response = self.admin_client.post_json("/admin/boardCategory/add", payload)
        self.expect_code(response, "新增分类")
        self.context["adminCategoryId"] = response.get("data")
        return "categoryId:{}".format(self.context["adminCategoryId"])

    def step_admin_update_category(self):
        payload = {
            "id": self.context["adminCategoryId"],
            "categoryName": "自动分类已更新 {}".format(self.unique_suffix),
            "categoryIcon": "",
            "sortOrder": 89,
            "categoryStatus": 0,
        }
        response = self.admin_client.post_json("/admin/boardCategory/update", payload)
        self.expect_code(response, "更新分类")
        return "categoryId:{}".format(self.context["adminCategoryId"])

    def step_admin_add_board(self):
        payload = {
            "categoryId": self.context["adminCategoryId"],
            "boardName": "自动板块 {}".format(self.unique_suffix),
            "boardCode": "auto_board_{}".format(self.unique_suffix),
            "boardIcon": "",
            "boardDescription": "自动化测试板块 {}".format(self.unique_suffix),
            "sortOrder": 66,
            "boardStatus": 0,
            "postAuditFlag": 0,
            "commentAuditFlag": 0,
        }
        response = self.admin_client.post_json("/admin/board/add", payload)
        self.expect_code(response, "新增板块")
        self.context["adminBoardId"] = response.get("data")
        return "boardId:{}".format(self.context["adminBoardId"])

    def step_admin_update_board(self):
        payload = {
            "id": self.context["adminBoardId"],
            "categoryId": self.context["adminCategoryId"],
            "boardName": "自动板块已更新 {}".format(self.unique_suffix),
            "boardCode": "auto_board_{}".format(self.unique_suffix),
            "boardIcon": "",
            "boardDescription": "自动化测试板块已更新 {}".format(self.unique_suffix),
            "sortOrder": 67,
            "boardStatus": 0,
            "postAuditFlag": 0,
            "commentAuditFlag": 0,
        }
        response = self.admin_client.post_json("/admin/board/update", payload)
        self.expect_code(response, "更新板块")
        return "boardId:{}".format(self.context["adminBoardId"])

    def step_admin_delete_board(self):
        response = self.admin_client.post_json("/admin/board/delete", {"id": self.context["adminBoardId"]})
        self.expect_code(response, "删除板块")
        return "boardId:{}".format(self.context["adminBoardId"])

    def step_admin_delete_category(self):
        response = self.admin_client.post_json("/admin/boardCategory/delete", {"id": self.context["adminCategoryId"]})
        self.expect_code(response, "删除分类")
        return "categoryId:{}".format(self.context["adminCategoryId"])

    def step_admin_add_tag(self):
        payload = {
            "tagName": "自动标签 {}".format(self.unique_suffix),
            "sortOrder": 55,
            "tagStatus": 0,
        }
        response = self.admin_client.post_json("/admin/post/tag/add", payload)
        self.expect_code(response, "新增标签")
        self.context["adminTagId"] = response.get("data")
        return "tagId:{}".format(self.context["adminTagId"])

    def step_admin_list_tag_page(self):
        response = self.admin_client.get("/admin/post/tag/list/page", {"current": 1, "pageSize": 10})
        self.expect_code(response, "标签分页")
        records = (response.get("data") or {}).get("records") or []
        self.assert_true(len(records) > 0, "后台标签分页为空")
        return "count:{}".format(len(records))

    def step_admin_update_tag(self):
        payload = {
            "id": self.context["adminTagId"],
            "tagName": "自动标签已更新 {}".format(self.unique_suffix),
            "sortOrder": 56,
            "tagStatus": 0,
        }
        response = self.admin_client.post_json("/admin/post/tag/update", payload)
        self.expect_code(response, "更新标签")
        return "tagId:{}".format(self.context["adminTagId"])

    def step_admin_delete_tag(self):
        response = self.admin_client.post_json("/admin/post/tag/delete", {"id": self.context["adminTagId"]})
        self.expect_code(response, "删除标签")
        return "tagId:{}".format(self.context["adminTagId"])

    def step_admin_list_post_page(self):
        response = self.admin_client.get("/admin/post/list/page", {"current": 1, "pageSize": 10})
        self.expect_code(response, "后台帖子分页")
        records = (response.get("data") or {}).get("records") or []
        self.assert_true(len(records) > 0, "后台帖子分页为空")
        return "count:{}".format(len(records))

    def step_admin_audit_post(self):
        payload = {
            "id": self.context["primaryPostId"],
            "auditStatus": AUDIT_STATUS_PASS,
            "auditRemark": "自动化审核通过 {}".format(self.unique_suffix),
        }
        response = self.admin_client.post_json("/admin/post/audit", payload)
        self.expect_code(response, "后台审核帖子")
        return "postId:{}".format(self.context["primaryPostId"])

    def step_admin_update_top(self):
        response = self.admin_client.post_json("/admin/post/update/top", {"id": self.context["primaryPostId"], "flagValue": FLAG_YES})
        self.expect_code(response, "后台置顶帖子")
        return "postId:{}".format(self.context["primaryPostId"])

    def step_admin_update_essence(self):
        response = self.admin_client.post_json("/admin/post/update/essence", {"id": self.context["primaryPostId"], "flagValue": FLAG_YES})
        self.expect_code(response, "后台精华帖子")
        return "postId:{}".format(self.context["primaryPostId"])

    def step_admin_list_comment_page(self):
        response = self.admin_client.get("/admin/comment/list/page", {"current": 1, "pageSize": 10})
        self.expect_code(response, "后台评论分页")
        records = (response.get("data") or {}).get("records") or []
        self.assert_true(len(records) > 0, "后台评论分页为空")
        return "count:{}".format(len(records))

    def step_admin_audit_comment(self):
        response = self.admin_client.post_json("/admin/comment/audit", {"id": self.context["primaryCommentId"], "auditStatus": AUDIT_STATUS_PASS})
        self.expect_code(response, "后台审核评论")
        return "commentId:{}".format(self.context["primaryCommentId"])

    def step_admin_list_report_page(self):
        response = self.admin_client.get("/admin/report/list/page", {"current": 1, "pageSize": 10})
        self.expect_code(response, "后台举报分页")
        records = (response.get("data") or {}).get("records") or []
        self.assert_true(len(records) > 0, "后台举报分页为空")
        return "count:{}".format(len(records))

    def step_admin_process_report(self):
        payload = {
            "id": self.context["reportId"],
            "processStatus": PROCESS_STATUS_DONE,
            "processRemark": "自动化处理 {}".format(self.unique_suffix),
        }
        response = self.admin_client.post_json("/admin/report/process", payload)
        self.expect_code(response, "后台处理举报")
        return "reportId:{}".format(self.context["reportId"])

    def step_admin_list_user_page(self):
        response = self.admin_client.post_json("/admin/user/list/page", {"current": 1, "pageSize": 10})
        self.expect_code(response, "后台用户分页")
        records = (response.get("data") or {}).get("records") or []
        self.assert_true(len(records) > 0, "后台用户分页为空")
        return "count:{}".format(len(records))

    def step_admin_update_user_status(self):
        response = self.admin_client.post_json("/admin/user/update/status", {
            "id": self.context["normalLoginUser"]["id"],
            "userStatus": DISABLED_USER_STATUS,
        })
        self.expect_code(response, "后台更新用户状态")
        return "uid:{}".format(self.context["normalLoginUser"]["id"])

    def step_admin_update_user_mute(self):
        mute_end_time = "2030-01-01T00:00:00.000+08:00"
        response = self.admin_client.post_json("/admin/user/update/mute", {
            "id": self.context["normalLoginUser"]["id"],
            "muteEndTime": mute_end_time,
        })
        self.expect_code(response, "后台更新用户禁言")
        return "uid:{} muteEndTime:{}".format(self.context["normalLoginUser"]["id"], mute_end_time)

    def step_admin_list_system_config(self):
        response = self.admin_client.get("/admin/system/config/list")
        self.expect_code(response, "后台查询系统配置列表")
        config_list = response.get("data") or []
        self.assert_true(len(config_list) > 0, "系统配置列表为空")
        self.context["systemConfigId"] = config_list[0]["id"]
        self.context["systemConfigValue"] = config_list[0].get("configValue")
        return "configId:{}".format(self.context["systemConfigId"])

    def step_admin_list_system_config_page(self):
        response = self.admin_client.get("/admin/system/config/list/page", {"current": 1, "pageSize": 10})
        self.expect_code(response, "后台查询系统配置分页")
        records = (response.get("data") or {}).get("records") or []
        self.assert_true(len(records) > 0, "系统配置分页为空")
        return "count:{}".format(len(records))

    def step_admin_update_system_config(self):
        payload = {
            "id": self.context["systemConfigId"],
            "configValue": self.context["systemConfigValue"],
            "configStatus": 0,
            "remark": "自动化更新 {}".format(self.unique_suffix),
        }
        response = self.admin_client.post_json("/admin/system/config/update", payload)
        self.expect_code(response, "后台更新系统配置")
        return "configId:{}".format(self.context["systemConfigId"])

    def step_admin_list_file_page(self):
        response = self.admin_client.get("/admin/file/list/page", {"current": 1, "pageSize": 20})
        self.expect_code(response, "后台文件分页")
        records = (response.get("data") or {}).get("records") or []
        self.assert_true(len(records) > 0, "后台文件分页为空")
        return "count:{}".format(len(records))

    def step_admin_delete_file(self):
        response = self.admin_client.post_json("/admin/file/delete", {"id": self.context["postCoverFile"]["fileId"]})
        self.expect_code(response, "后台删除文件")
        return "fileId:{}".format(self.context["postCoverFile"]["fileId"])

    def step_admin_list_operation_log(self):
        response = self.admin_client.get("/admin/system/operationLog/list/page", {"current": 1, "pageSize": 20})
        self.expect_code(response, "后台操作日志分页")
        records = (response.get("data") or {}).get("records") or []
        self.assert_true(len(records) > 0, "操作日志分页为空")
        return "count:{}".format(len(records))

    def step_admin_statistics_overview(self):
        response = self.admin_client.get("/admin/statistics/overview")
        self.expect_code(response, "后台统计概览")
        overview = response.get("data") or {}
        self.assert_true("totalUserCount" in overview, "统计概览字段缺失")
        return "totalUserCount:{}".format(overview.get("totalUserCount"))

    def step_admin_statistics_trend(self):
        response = self.admin_client.get("/admin/statistics/trend", {"dayCount": 7})
        self.expect_code(response, "后台统计趋势")
        trend_list = response.get("data") or []
        self.assert_true(len(trend_list) > 0, "统计趋势为空")
        return "count:{}".format(len(trend_list))

    def step_admin_statistics_board_rank(self):
        response = self.admin_client.get("/admin/statistics/board/rank", {"rankSize": 5})
        self.expect_code(response, "后台板块排行")
        rank_list = response.get("data") or []
        self.assert_true(len(rank_list) > 0, "板块排行为空")
        return "count:{}".format(len(rank_list))

    def upload_test_file(self, client, path, filename):
        content = base64.b64decode(TEST_IMAGE_BASE64)
        response = client.post_multipart(path, {
            "file": {
                "filename": filename,
                "content": content,
                "contentType": "image/png",
            }
        })
        self.expect_code(response, "上传文件 {}".format(path))
        data = response.get("data") or {}
        self.assert_true(data.get("fileId") is not None, "上传文件返回 fileId 为空")
        return data

    def parse_datasource_config(self):
        content = (self.resource_root / "application-local.yml").read_text(encoding="utf-8")
        url_match = re.search(r"url:\s*(jdbc:mysql://[^\n]+)", content)
        username_match = re.search(r"username:\s*([^\n]+)", content)
        password_match = re.search(r"password:\s*([^\n]+)", content)
        self.assert_true(url_match is not None, "未找到数据库URL配置")
        self.assert_true(username_match is not None, "未找到数据库用户名配置")
        self.assert_true(password_match is not None, "未找到数据库密码配置")
        return {
            "url": url_match.group(1).strip(),
            "username": username_match.group(1).strip(),
            "password": password_match.group(1).strip(),
        }

    def promote_user_to_admin(self, datasource, user_account):
        driver_jar = self.find_mysql_driver_jar()
        temp_dir = Path(tempfile.mkdtemp(prefix="forum_admin_promote_"))
        java_file = temp_dir / "AdminPromoter.java"
        class_name = "AdminPromoter"
        java_file.write_text(self.build_admin_promoter_java(), encoding="utf-8")
        compile_cmd = ["javac", "-cp", driver_jar, str(java_file)]
        self.run_subprocess(compile_cmd, "编译管理员提升工具失败")
        run_cmd = [
            "java",
            "-cp",
            "{}:{}".format(temp_dir, driver_jar),
            class_name,
            datasource["url"],
            datasource["username"],
            datasource["password"],
            user_account,
        ]
        self.run_subprocess(run_cmd, "执行管理员提升工具失败")

    def build_admin_promoter_java(self):
        return """
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class AdminPromoter {
    public static void main(String[] args) throws Exception {
        if (args.length < 4) {
            throw new IllegalArgumentException("args missing");
        }
        String url = args[0];
        String username = args[1];
        String password = args[2];
        String userAccount = args[3];
        Class.forName("com.mysql.cj.jdbc.Driver");
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(
                 "update forum_user set userRole = 'admin', userStatus = 0 where userAccount = ?")) {
            preparedStatement.setString(1, userAccount);
            int rowCount = preparedStatement.executeUpdate();
            if (rowCount <= 0) {
                throw new IllegalStateException("promote admin failed");
            }
        }
    }
}
""".strip()

    def find_mysql_driver_jar(self):
        repo_dir = Path.home() / ".m2" / "repository" / "com" / "mysql" / "mysql-connector-j"
        jar_list = sorted(repo_dir.rglob("mysql-connector-j-*.jar"))
        jar_list = [item for item in jar_list if not str(item).endswith("-sources.jar")]
        self.assert_true(len(jar_list) > 0, "未找到 MySQL JDBC 驱动")
        return str(jar_list[-1])

    def run_subprocess(self, cmd, error_message):
        process = subprocess.Popen(cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        stdout, stderr = process.communicate()
        if process.returncode != 0:
            raise TestFailure("{} stdout:{} stderr:{}".format(
                error_message,
                stdout.decode("utf-8", errors="ignore"),
                stderr.decode("utf-8", errors="ignore"),
            ))

    def expect_code(self, response, step_name):
        if response.get("code") != SUCCESS_CODE:
            raise TestFailure("{} 返回失败 code:{} message:{} httpStatus:{} url:{}".format(
                step_name,
                response.get("code"),
                response.get("message"),
                response.get("httpStatus"),
                response.get("requestUrl"),
            ))

    def assert_true(self, condition, message):
        if not condition:
            raise TestFailure(message)


def parse_args():
    parser = argparse.ArgumentParser(description="Communityforum 后端接口自动化测试脚本")
    parser.add_argument("--base-url", default="http://localhost:8120/api", help="后端接口基础地址")
    parser.add_argument("--report-file", default="scripts/forum_api_test_report.json", help="测试报告输出路径")
    return parser.parse_args()


def main():
    args = parse_args()
    project_root = Path(__file__).resolve().parents[1]
    tester = ForumApiTester(args.base_url, project_root)
    start_time = time.time()
    summary = OrderedDict()
    summary["baseUrl"] = args.base_url
    summary["startTime"] = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    try:
        result = tester.run()
        summary.update(result)
        summary["success"] = True
    except Exception as error:
        result = tester.logger.build_summary()
        summary.update(result)
        summary["success"] = False
        summary["error"] = str(error)
        print("\n测试中断 {}".format(error))
    summary["durationSeconds"] = round(time.time() - start_time, 2)
    report_path = project_root / args.report_file
    report_path.parent.mkdir(parents=True, exist_ok=True)
    report_path.write_text(json.dumps(summary, ensure_ascii=False, indent=2), encoding="utf-8")
    print("\n测试报告已写入 {}".format(report_path))
    if not summary.get("success"):
        sys.exit(1)


if __name__ == "__main__":
    main()
