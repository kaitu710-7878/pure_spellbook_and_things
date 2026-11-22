# Pure Spellbook and things / ピュアスペルブック＆シングス

- [English](#english)
- [日本語](#日本語)

---

## English

An addon mod for **Iron's Spells 'n Spellbooks** (Minecraft 1.20.1 Forge) introducing the **Pure** magic school, **Pure Scroll Forge**, **Pure Spellbook**, **Pure Rune / Pure Upgrade Orb**, **Akashic Record**, and 15 unique Legendary Pure spells.

### Requirements

- **Minecraft**: 1.20.1
- **Minecraft Forge**: 47.3.0+
- **Iron's Spells 'n Spellbooks**: `1.20.1-3.16.3`
- **L2Library**: Required

> **Note**: L2Complements is NOT required as a dependency; its purification logic is referenced cleanly using L2Library under dual licensing.

### Installation

1. Install Minecraft Forge 1.20.1.
2. Place **Iron's Spells 'n Spellbooks**, **L2Library** (and their required libraries such as Curios, GeckoLib, PlayerAnimator, Caelus), and `pure_spellbook_and_things-<version>.jar` into your `mods` folder.
3. Launch Minecraft.

### Features

#### Blocks & Items
- **Pure Scroll Forge**
  - Crafted with `Scroll Forge` + 8 `Arcane Essence`.
  - Operates using **Ink only** (paper and focus slots are visual-only).
  - Ignores normal crafting restrictions, allowing treasure-only and Pure scrolls to be forged. Single-rarity / max-level-1 spells can also be forged using higher-rarity ink (such as Legendary Ink).
- **Pure Spellbook**
  - Crafted with `Book` + 8 `Arcane Essence`.
  - Simple 12-slot spellbook with no special equipment stat bonuses.
- **Pure Rune**
  - Crafted (shapeless) with 1 `Blank Rune` + 6 `Arcane Essence` + 2 `Cinder Essence`.
- **Pure Upgrade Orb**
  - Crafted with 1 `Upgrade Orb` surrounded by 8 `Pure Runes`.
  - Can be applied to armor and spellbooks just like other upgrade orbs, granting **+5% Pure Spell Power**.

#### Pure School & Spells (15 Spells)
All Pure spells are **Legendary (Max Level 1)**, cannot be crafted in a normal Scroll Forge, cannot be cast by enemies, and can be obtained via Pure Scroll Forge, loot tables, or Creative mode.

1. **Empowered**: +40% Spell Power, +85% Attack Damage, and uninterruptible casting.
2. **Weapon Interaction**: Adds held weapon base damage to all spell damage.
3. **Soul Shaving**: Every hit (including during i-frames and multi-hit spells) reduces target HP directly by `1% of Max HP` (clamped 1–40) via `setHealth`.
4. **Healing Attack**: Melee hits restore 3 Mana; spell hits restore 1 HP.
5. **Overload Overclocking**: No cooldown/mana/cast time. Resets all spell cooldowns and restores full mana while applying/stacking the neutral `Overload` effect (-10% Max Mana per level, immune to purification).
6. **Vitality Recovery**: Restores 5 hunger, 5 saturation, 10 HP (scaled by spell power), and cleanses harmful debuffs.
7. **Sustained Purification**: Grants `Purification` for 15 seconds, continuously clearing harmful effects.
8. **Overheel**: Full heal + grants `OverHP` attribute (10 * spell power, cleared on death). Missing HP is immediately healed by consuming `OverHP`.
9. **Evasive Movement**: Teleports 10 blocks forward along sight and grants 1 hit of non-teleporting Evasion (`Evasive Dodge`) for 5 seconds.
10. **Akashic Record: Access**: 400 Mana, 5s cast, no cooldown (permanently locked at Lv.1). Opens an ender-chest-style 4-page storage (`6×3` slots per page) for ISS spell scrolls.
11. **Akashic Record: Execution I**: Permanently locked at Lv.1. Simultaneously casts all scrolls stored on Page 1 at the end of casting. Combined mana (`250 + sum`), cast time (`sum`), cooldown (`sum`), and each spell's level/original school power scaling are read directly from the stored scroll NBT.
12. **Akashic Record: Execution II**: Same as Execution I, executing Page 2.
13. **Akashic Record: Execution III**: Same as Execution I, executing Page 3.
14. **Akashic Record: Execution IV**: Same as Execution I, executing Page 4.
15. **Extension**: 200 Mana, 2s cast, 2m cooldown (supports addon max-level extension). Extends the duration of all active beneficial buffs by 1m30s at the same amplifier (scales with Spell Power and spell level).
16. **Legacy of the past: Echo Strike**: 90 Mana, instant cast, 60s cooldown (Legendary max Lv.1, equivalent to ISS 3.4.0.4 Echoing Strikes Lv.5; supports addon tier scaling; uses Pure Spell Power). Melee and arrow hits unleash delayed 3-block AoE echoes.
17. **Legacy of the past: Oakskin**: 50 Mana, instant cast, 35s cooldown (Legendary max Lv.1, equivalent to ISS 3.4.0.4 Oakskin Lv.8; supports addon tier scaling; uses Pure Spell Power). Grants 50% damage reduction at Lv.1 without slowness.
18. **Legacy of the past: Haste**: 100 Mana, 1.5s cast, 45s cooldown (Legendary max Lv.1, equivalent to ISS 3.4.0.4 Haste Lv.6; supports addon tier scaling; uses Pure Spell Power). Boosts movement speed, attack speed, mana regen, and cast time reduction (+60% at Lv.1) for up to 5 allies in a 3-block radius.

#### Akashic Record Notes
- **Processing Order**: Slots in each page (`6×3`) are processed **from top-left to bottom-right** (left-to-right, top row to bottom row).
- **Destructible Projectiles**: Even when spells are invoked simultaneously, **destructible projectiles or entities that break upon taking damage (such as ISS's default Wisps)** may be hit and destroyed by the attack hitboxes of other spells fired in the same execution, causing them to effectively fizzle.
- **Non-storable Spells**: Akashic Record spells, Recast/ammunition-style spells (`getRecastCount > 0`), and Continuous-channel spells (`CastType.CONTINUOUS`) cannot be stored in the Akashic Record.

### License
Dual-licensed under **GPL-3.0-or-later AND LGPL-2.1-or-later**. See [LICENSE](LICENSE).

---

## 日本語

**Iron's Spells 'n Spellbooks**（Minecraft 1.20.1 Forge）向けのアドオンMODです。**無垢（Pure）**属性の魔法、**ピュアスクロールフォージ**、**ピュアスペルブック**、**無垢のルーン・無垢のオーブ**、**アカシックレコード**、および15種類のユニークなレジェンダリー呪文を追加します。

### 前提環境・依存MOD

- **Minecraft**: 1.20.1
- **Minecraft Forge**: 47.3.0+
- **Iron's Spells 'n Spellbooks**: `1.20.1-3.16.3`
- **L2Library**: 必須

> **注意**: L2Complements 本体は依存関係に含まれません。浄化ロジックのみ L2Library を用いて実装しています。

### 導入方法

1. Minecraft Forge 1.20.1 を導入します。
2. **Iron's Spells 'n Spellbooks**、**L2Library**（および Curios, GeckoLib, PlayerAnimator, Caelus などの各前提ライブラリ）、本MODのJARファイルを `mods` フォルダに配置します。
3. Minecraft を起動します。

### 追加要素

#### ブロック・アイテム
- **ピュアスクロールフォージ**
  - スクロールフォージ + 秘術のエッセンス×8 でクラフト。
  - **インクのみ**で動作します（紙と属性素材スロットは見た目のみ）。
  - 通常のクラフト可否フラグを無視し、お宝専用スクロールや無垢呪文も作成可能です。レアリティが1種類のみ・最大レベル1の呪文も、最小レアリティ以上の上位インク（レジェンダリーインク等）で作れます。
- **ピュアスペルブック**
  - 本 + 秘術のエッセンス×8 でクラフト。
  - 装備時効果を持たないシンプルな12スロット魔導書。
- **無垢のルーン**
  - ブランク・ルーン×1 + 秘術のエッセンス×6 + 燃え殻のエッセンス×2 でクラフト（不定形）。
- **無垢のオーブ**
  - 空白のオーブを中心に無垢のルーン×8 でクラフト。
  - 他のオーブと同様に防具やスペルブック等へ付与でき、**無垢魔法威力 +5%** を得られます。

#### 無垢属性と呪文 (15種)
すべての無垢呪文は**レジェンダリー（通常最大レベル1）**で、通常のスクロールフォージでは作成できず、敵も詠唱しません。ピュアスクロールフォージ、チェスト等のルートテーブル、クリエイティブモードから入手できます。

1. **エンパワード**: 魔法威力+40%、近接攻撃力+85%、詠唱中断無効を付与。
2. **ウェポンインタラクション**: 手に持つ武器の基礎攻撃力をすべての魔法ダメージに加算。
3. **魂削り**: すべての攻撃ヒット（無敵時間中や多段ヒット含む）ごとに、対象HPを「現在HP − 最大HPの1%（最小1、最大40）」へセットヘルスで直接削ります。
4. **ヒーリングアタック**: 近接ヒット時マナ3回復、魔法ヒット時HP1回復。
5. **オーバーロードオーバークロッキング**: クールダウン・マナ・詠唱時間なし。全クールダウンをリセットしマナを全回復する代わりに、浄化不可の中立エフェクト「オーバーロード」（レベル×10%最大マナ減少）を付与・累積。
6. **バイタリティリカバリー**: 満腹度5・隠し満腹度5・HP10（魔法威力依存）を回復し、デバフを浄化。
7. **持続的浄化**: 15秒間デバフを消去し続ける「ピュリフィケーション」効果を付与。
8. **オーバーヒール**: HPを全回復し、`オーバーHP` 属性（10×魔法威力、死亡時消滅）を付与。HPが減ると `オーバーHP` を消費して即座に回復。
9. **イヴェイシブムーブメント**: 視線方向へ10ブロックテレポートし、5秒間のテレポートしない回避バフ（`イヴェイシブドッジ`、1回分）を付与。
10. **アカシックレコード:アクセス**: 消費マナ400、詠唱5秒、クールダウンなし（レベル1固定）。エンダーチェスト方式で管理される全4ページ（各6×3スロット）のスクロール保管庫「アカシックレコード」を開きます。
11. **アカシックレコード:実行1**: レベル1固定。ページ1に格納したスクロールの効果を詠唱終了時にすべて同時に発動します。消費マナ（`250 + 合算`）・詠唱時間（`合算`）・クールダウン（`合算`）・および各呪文のレベルや本来の属性威力参照は格納スクロールのNBTをそのまま引き継ぎます。
12. **アカシックレコード:実行2**: 実行1のページ2版。
13. **アカシックレコード:実行3**: 実行1のページ3版。
14. **アカシックレコード:実行4**: 実行1のページ4版。
15. **延長**: 消費マナ200、詠唱2秒、クールダウン2分（アドオンによるレベル上限拡張に対応）。自身の持つすべてのバフを同じ効果レベルのまま1分30秒延長します（魔法威力・呪文レベルで延長時間が増加）。
16. **過去の遺産:エコーストライク**: 消費マナ90、即時発動、クールダウン60秒（レジェンダリー最大Lv.1でISS 3.4.0.4時代Lv.5相当、アドオンによるレベル上昇対応、無垢魔法威力参照）。近接・弓攻撃のヒット時に半径3ブロックの遅延範囲エコーを発生させます。
17. **過去の遺産:オークスキン**: 消費マナ50、即時発動、クールダウン35秒（レジェンダリー最大Lv.1でISS 3.4.0.4時代Lv.8相当、アドオンによるレベル上昇対応、無垢魔法威力参照）。移動速度低下なしで割合ダメージ軽減（Lv.1で50%軽減）を付与します。
18. **過去の遺産:ヘイスト**: 消費マナ100、詠唱1.5秒、クールダウン45秒（レジェンダリー最大Lv.1でISS 3.4.0.4時代Lv.6相当、アドオンによるレベル上昇対応、無垢魔法威力参照）。対象の周囲3ブロック内の最大5体に移動速度・攻撃速度・マナ回復・詠唱時間短縮のバフ（Lv.1で+60%）を付与します。

#### アカシックレコード使用上の注意
- **処理順**: 各ページ（`6×3`）に格納されたスクロールは **左上から右下へ向かって順に** 処理されます。
- **破壊されうる弾幕・召喚物について**: 同時発動自体は正常に行われていても、**攻撃を受けると破壊されるタイプの弾幕・召喚物（ISS デフォルトのウィスプ等）は、同時に発射した他の弾幕・呪文の攻撃判定によって破壊され、実質不発になることがあります。** 格納する呪文の順番や組み合わせにご注意ください。
- **格納不可の呪文**: 一部呪文（アカシックレコード系、リキャスト/弾薬系、継続照射系）はアカシックレコードに格納できません。

### ライセンス
**GPL-3.0-or-later AND LGPL-2.1-or-later** のデュアルライセンスです。詳細は [LICENSE](LICENSE) を参照してください。
