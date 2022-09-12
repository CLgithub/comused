---
title: "Git同一项目多个仓库"
date: 2022-03-02T23:05:28+08:00
draft: true
---

管理Macos中的SSH Key

```bash
# sshkey会放在～/.ssh目录下
cd ~/.ssh
# 对不同账户，生成不同的key
ssh-keygen -t rsa -C "CLgithub" -f github_rsa
ssh-keygen -t rsa -C "CLgitlab" -f gitlab_rsa
```

在.ssh目录下，添加配置文件config，文件内容如下

```bash
# github
Host github.com
HostName github.com
PreferredAuthentications publickey
IdentityFile ~/.ssh/github_rsa

# gitlab
Host gitlab.com
HostName gitlab.com
PreferredAuthentications publickey
IdentityFile ~/.ssh/company_rsa
```

在项目中，添加两个远程仓库

```bash
git remote add originGithub git@xxxxx.git
git remote add originGitlab https://xxxxx.git
# 查看远程仓库详细
git remote -v
# github选用ssh协议
```

将对应的两个公钥添加到对应的远程仓库中

相关链接

https://www.jianshu.com/p/4cd46619b3a5

https://segmentfault.com/a/1190000017945878

https://www.jianshu.com/p/194f787998c1

https://git-scm.com/book/zh/v2/服务器上的-Git-生成-SSH-公钥
