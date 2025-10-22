这是什么？​

Yinonoupdated 是一个“管控方块更新”的小工具。你可以在指定世界里，一键关闭这些会把地图弄乱的变化：


物理更新：比如沙子会掉、红石被连带更新
流体流动：水/岩浆到处扩散
生长/蔓延/生成：作物长高、草/菌丝/苔藓扩散、树长出来



为什么有用？​

地图更可控：由于JAVA版没有禁止方块更新的命令所以这个插件出现了。
可控不死板：可以把某些方块放进白名单，它们永远不过滤。
贴心小特性：如果一个方块上方一格是白名单（例如红石线、火把、甘蔗），就放行它的更新（我们称为“白名单底座”），确保你想要的玩法不会被误拦。
边看边改：提供游戏内法杖，你拿着法杖对着方块——左键加入、右键删除白名单或过滤清单，马上生效，不用重启、不用改文件。


三步上手（超简单）​

安装：把插件扔进 plugins，启动一次服务器自动生成配置。
设置（config.yml）：
进服使用法杖:
/yinonoupdated tool whitelist（白名单法杖）
/yinonoupdated tool filter（过滤清单法杖）
左键添加、右键删除，对着想要的方块直接改，立刻生效。


插件命令​
/yinonoupdated reload - 重载配置
/yinonoupdated toggle [世界名] - 对某世界开/关
/yinonoupdated tool whitelist|filter - 发法杖（左加右删）
/yinonoupdated addwl|delwl [方块ID] - 白名单增删
/yinonoupdated addfilter|delfilter [方块ID] - 过滤清单增删

插件交流群：https://qm.qq.com/q/b8h3DGp7s4​
