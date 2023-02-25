对commit 信息简单约定如下：

```
<type>: <content>
<scope>
```

三个字段: type (必须)， content(必须) 和 scope(可选)。

1. type用于说明该 commit 的类型的：

   feat: 新功能(feature)

   fix: 修复 bug

   docs: 文档(documents)

   style: 代码格式(不影响代码运行的格式变动，注意不是指 CSS 的修改)

   refactor: 重构(既不是新增功能，也不是修改 bug 的代码变动)

   test: 提交测试代码(单元测试，集成测试等)

   chore: 构建或辅助工具的变动

   misc: 一些未归类或不知道将它归类到什么方面的提交

2. content概述提交内容。
3. scope备注提交范围，如服务器端、app端或网页端。可以省略。

[参考](https://www.yuque.com/fe9/basic/nruxq8#6c228def)