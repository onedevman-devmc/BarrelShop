# BarrelShop

A simple user & admin shops plugin designed for SMP.

### Download

You can download the plugin from [releases](https://github.com/onedevman-devmc/BarrelShop/releases)

### Configuration

*Nothing for the moment*

### Permissions

| Permission      | Description                   |
| --------------- | ----------------------------- |
| mc.barrelshop.* | Gives all plugin permissions. |

<details>
    <summary><strong><i>mc.barrelshop.*</i></strong></summary>

| Permission           | Description                          |
| -------------------- | ------------------------------------ |
| mc.barrelshop.shop.* | Gives all shops related permissions. |

</details>

<details open>
    <summary><strong><i>mc.barrelshop.shop.*</i></strong></summary>

| Permission                 | Description                                 |
| -------------------------- | ------------------------------------------- |
| mc.barrelshop.shop.admin.* | Gives all admin-shops related permissions.  |
| mc.barrelshop.shop.self.*  | Gives all self-shops related permissions.   |
| mc.barrelshop.shop.other.* | Gives all others-shops related permissions. |

</details>

<details>
    <summary><strong><i>mc.barrelshop.shop.admin.*</i></strong></summary>

| Permission                      | Description                   |
| ------------------------------- | ----------------------------- |
| mc.barrelshop.shop.admin.create | Allows to create admin shops. |
| mc.barrelshop.shop.admin.delete | Allows to delete admin shops. |
| mc.barrelshop.shop.admin.manage | Allows to manage admin shops. |

</details>

<details>
    <summary><strong><i>mc.barrelshop.shop.self.*</i></strong></summary>

| Permission                     | Description                     |
| ------------------------------ | ------------------------------- |
| mc.barrelshop.shop.self.create | Allows to create its own shops. |
| mc.barrelshop.shop.self.delete | Allows to delete its own shops. |
| mc.barrelshop.shop.self.manage | Allows to manage its own shops. |

</details>

<details>
    <summary><strong><i>mc.barrelshop.shop.other.*</i></i></strong></summary>

| Permission                      | Description                           |
| ------------------------------- | ------------------------------------- |
| mc.barrelshop.shop.other.delete | Allows to delete other players shops. |
| mc.barrelshop.shop.other.manage | Allows to manage other players shops. |

</details>

### Usage

#### Create

To create a shop (or admin shop), place a `barrel` and a `sign` on one of the `barrel` side.
On the first line of the `sign`, write `[shop]` (or `[admin shop]`) and close the sign.
Done! You've created your first shop ;\) !

#### Delete

To delete a shop, simply break the `barrel` or the `sign`.

#### Manage

To manage a shop, click on the `sign`, you'll have a light interface with all settings on the first rows and a row dedicated to trades.

By clicking on one of the trades in the dedicated row, you can manage this trade.

There's a limit of 9 trades by shop (even for admin shops).

Player's shop requires to add container items (chest, barrel, shulker box, ...) to each trade to store trade earnings and stocks.

To add them, just use the dedicated slots in the trade management menu.

These container items will be updated after each trade and drop if the shop is deleted.

### Trade

To trade with your shop, players can open a trade interface (villager-like) by clicking on the barrel.

### Security

Shops can't be destroyed by explosions, fire or pistons.

They can be managed only by authorized players.