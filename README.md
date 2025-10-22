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

配置：
# 插件在哪些世界里生效
enabled-worlds:
  - world

block:
  # 物理/邻接更新：比如沙子会掉落、红石受邻接触发、方块失去支撑等
  # 开启(true) = 拦截；关闭(false) = 不拦
  physics: true

  # 流体更新：水/岩浆流动、扩散
  fluids:  true

  # 生长/蔓延/生成：作物长高、草/菌丝/苔藓蔓延、树木生成等
  growth:  true

# 放行的材料（白名单，不受拦截）
# - 这些“方块本体”永远不过滤
# - 另外：若“某个方块上方一格”是白名单材料，这个“底座”也会被放行（白名单底座规则）
# - 下面默认放了常见红石组件，防止误拦造成红石断路
whitelist:
  - TORCH
  - REDSTONE_TORCH
  - REDSTONE_WIRE
  # - SUGAR_CANE       # 示例：想让甘蔗继续长就把作物本体加入白名单
  # - BAMBOO           # 示例：想让竹子继续长
  # - LEVER            # 示例：拉杆
  # - STONE_BUTTON     # 示例：按钮
  # - REPEATER         # 示例：中继器
  # - COMPARATOR       # 示例：比较器
  # - OBSERVER         # 示例：侦测器
  # - WALL_TORCH       # 示例：墙上火把（部分版本区分）

# 只拦这些材料（精确拦截）。留空 = 按上面的三大类型“全局拦截”
# 常见用法：
#   1）新手建议先留空[]，按类型总控，最省心
#   2）如果你只想拦“少数材料”，再开启下面的名单（例如只拦沙子/水/岩浆）
# 注意：
#   - 一旦这里不是空的[]，插件就“只会拦这里列出的材料”，其它材料即使属于开启的类型也不拦
materials-filter: []
  # 例子：只拦沙子掉落、水和岩浆流动，其它一律不拦
  # - SAND
  # - RED_SAND
  # - WATER
  # - LAVA
  # 例子：只拦草蔓延/菌丝蔓延（配合 block.growth: true 使用）
  # - GRASS_BLOCK
  # - MYCELIUM
  # - MOSS_BLOCK
