{toc}

# [提供打包插件jar](.idea/ideaPlugin.jar)   

## Intellij-common-plugin

### 项目初衷 
1. mybatis是目前最常用的框架，且idea没有提供插件支持
2. intellij插件库中有一款很棒的mybatis插件，但是是收费的($40左右，也不贵)
3. qunar自己开发的框架没有插件支持
4. 学习和开发intellij插件，可以提高工作效率

### 提供的功能
1. mybatis接口和xml跳转、<include><sql>等内部节点的关联跳转
2. mybatis的configuration、mapper文件校验，解析typeAliases等
3. 解析spring mybatis集成配置
3. mybatis自动代码提示，如：id、resultMap、${}、#{}等
4. spring的beans xml中校验qschedule配置项
5. 增加qschedule xml配置中method属性校验、自动提示、快速修复等

### 2016.12.03 v1.4.0更新
1. 增加interface创建xml文件intention, 及ui dialog
2. 增加interface方法创建xml statement intention
3. 增加xml文件中#{} ${}参数的关联跳转
4. 修复spring bean name重复问题

### 2016.12.05 v1.4.2更新
1. \#{} \${}参数跳转支持原始类型
2. 修复isMybatis方法可能死循环的问题
3. 解析module配置，获取resource路径（之前通过名称判断）

### TODO
1. 同一mapper class和xml中相同id或methodName的高亮error提示
4. parameterType属性的error改成高亮error提示
5. 增加新建接口generate对应mapper文件，以及新建mapper文件generate对应dao接口功能
6. 提升性能 提升性能 提升性能
1. 支持dubbo配置文件
6. and more ...

### 插件截图

![Alt text](.idea/snapshot/DeepinScrot-2141.png)

![Alt text](.idea/snapshot/screenshot_16384.png)

![Alt text](.idea/snapshot/screenshot_16385.png)

![Alt text](.idea/snapshot/screenshot_16386.png)

![Alt_text](.idea/snapshot/screenshot_16802.png)

![Alt_text](.idea/snapshot/screenshot_16801.png)

