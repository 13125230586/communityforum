# Communityforum 自动化接口测试脚本

## 一 说明

该脚本用于对社区论坛系统后端执行五个阶段的自动化接口测试：

- 第一阶段：基础检查
- 第二阶段：普通用户链路
- 第三阶段：内容互动链路
- 第四阶段：管理员后台链路
- 第五阶段：回归验证

注意：

- 脚本直接请求真实接口地址 `http://localhost:8120/api/...`
- `http://localhost:8120/api/doc.html` 只是文档页面 不作为脚本执行入口
- 脚本会自动创建测试用户 测试帖子 测试评论 测试分类 测试板块 测试标签
- 脚本会读取 `application-local.yml` 中的数据库配置 并自动把管理员测试用户提升为 `admin`

## 二 运行前提

- 后端服务已启动
- 本地 MySQL 已可用
- `application-local.yml` 中的数据源配置有效
- 本地 JDK 已安装 并可使用 `javac` `java`
- 本地 Maven 依赖目录中已存在 MySQL JDBC 驱动
- OSS 配置有效 否则上传相关步骤会失败

## 三 运行方式

在项目根目录执行：

```bash
cd /Users/hccmac/Communityforum/communityforum-backend
python3 scripts/forum_api_test.py
```

如果你的服务地址不是默认值：

```bash
python3 scripts/forum_api_test.py --base-url http://localhost:8120/api
```

## 四 输出结果

脚本执行完成后会输出：

- 控制台逐步通过或失败日志
- JSON 测试报告文件

默认报告文件：

- `scripts/forum_api_test_report.json`

## 五 失败排查建议

### 5.1 注册或登录失败

优先检查：

- 服务是否已启动
- `base-url` 是否正确
- 之前是否已存在同名用户

### 5.2 管理员提升失败

优先检查：

- `application-local.yml` 中的数据源配置是否正确
- 本地是否已下载 MySQL JDBC 驱动
- JDK 的 `javac` 是否可用

### 5.3 上传接口失败

优先检查：

- OSS 配置是否有效
- Bucket 是否存在
- AccessKey 是否可用

### 5.4 后台接口返回无权限

优先检查：

- 管理员提升步骤是否成功
- 管理员登录是否成功
- 是否使用了管理员 Session 继续调用后台接口
