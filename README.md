# Pure Spellbook and things

An addon mod for **Iron's Spells 'n Spellbooks** (Minecraft 1.20.1 Forge) introducing the **Pure (無垢)** magic school, **Pure Scroll Forge**, **Pure Spellbook**, **Pure Rune / Pure Upgrade Orb**, **Akashic Record**, and 15 unique Legendary Pure spells.

**Iron's Spells 'n Spellbooks**（Minecraft 1.20.1 Forge）向けのアドオンMODです。**無垢（Pure）**属性の魔法、**ピュアスクロールフォージ**、**ピュアスペルブック**、**無垢のルーン・無垢のオーブ**、**アカシックレコード**、および15種類のユニークなレジェンダリー呪文を追加します。

---

## Requirements / 前提環境・依存MOD

- **Minecraft**: 1.20.1
- **Minecraft Forge**: 47.3.0+
- **Iron's Spells 'n Spellbooks**: `1.20.1-3.16.3`
- **L2Library**: Required / 必須

> **Note / 注意**: L2Complements is NOT required as a dependency; its purification logic is referenced cleanly using L2Library under dual licensing.  
> L2Complements 本体は依存関係に含まれません。浄化ロジックのみ L2Library を用いて実装しています。

---

## Installation / 導入方法

1. Install Minecraft Forge 1.20.1. / Minecraft Forge 1.20.1 を導入します。
2. Place **Iron's Spells 'n Spellbooks**, **L2Library** (and their required libraries such as Curios, GeckoLib, PlayerAnimator, Caelus), and `pure_spellbook_and_things-<version>.jar` into your `mods` folder.  
   **Iron's Spells 'n Spellbooks**、**L2Library**（および各前提ライブラリ）、本MODのJARファイルを `mods` フォルダに配置します。
3. Launch Minecraft. / Minecraft を起動します。

---

## Features / 追加要素

### Blocks & Items / ブロック・アイテム
- **Pure Scroll Forge / ピュアスクロールフォージ**
  - Crafted with `Scroll Forge` + 8 `Arcane Essence`. / スクロールフォージ + 秘術のエッセンス×8 でクラフト。
  - Operates using **Ink only** (paper and focus slots are visual-only). / **インクのみ**で動作します（紙と属性素材スロットは見た目のみ）。
  - Ignores normal crafting restrictions, allowing treasure-only and Pure scrolls to be forged. Single-rarity / max-level-1 spells can also be forged using higher-rarity ink (such as Legendary Ink). / 通常のクラフト可否フラグを無視し、お宝専用スクロールや無垢呪文も作成可能です。レアリティが1種類のみ・最大レベル1の呪文も、最小レアリティ以上の上位インク（レジェンダリーインク等）で作れます。
- **Pure Spellbook / ピュアスペルブック**
  - Crafted with `Book` + 8 `Arcane Essence`. / 本 + 秘術のエッセンス×8 でクラフト。
  - Simple 12-slot spellbook with no special equipment stat bonuses. / 装備時効果を持たないシンプルな12スロット魔導書。
- **Pure Rune / 無垢のルーン**
  - Crafted (shapeless) with 1 `Blank Rune` + 6 `Arcane Essence` + 2 `Cinder Essence`. / ブランク・ルーン×1 + 秘術のエッセンス×6 + 燃え殻のエッセンス×2 でクラフト。
- **Pure Upgrade Orb / 無垢のオーブ**
  - Crafted with 1 `Upgrade Orb` surrounded by 8 `Pure Runes`. / 空白のオーブを中心に無垢のルーン×8 でクラフト。
  - Can be applied to armor and spellbooks just like other upgrade orbs, granting **+5% Pure Spell Power (`無垢魔法威力`)**. / 他のオーブと同様に防具やスペルブック等へ付与でき、**無垢魔法威力 +5%** を得られます。

---

### Pure School & Spells / 無垢属性と呪文 (15 Spells)
All Pure spells are **Legendary (Max Level 1)**, cannot be crafted in a normal Scroll Forge, cannot be cast by enemies, and can be obtained via Pure Scroll Forge, loot tables, or Creative mode.

すべての無垢呪文は**レジェンダリー（通常最大レベル1）**で、通常のスクロールフォージでは作成できず、敵も詠唱しません。ピュアスクロールフォージ、チェスト等のルートテーブル、クリエイティブモードから入手できます。

1. **Empowered / エンパワード**: +40% Spell Power, +85% Attack Damage, and uninterruptible casting. / 魔法威力+40%、近接攻撃力+85%、詠唱中断無効を付与。
2. **Weapon Interaction / ウェポンインタラクション**: Adds held weapon base damage to all spell damage. / 手に持つ武器の基礎攻撃力をすべての魔法ダメージに加算。
3. **Soul Shaving / 魂削り**: Every hit (including during i-frames and multi-hit spells) reduces target HP directly by `1% of Max HP` (clamped 1–40) via `setHealth`. / すべての攻撃ヒット（無敵時間中や多段ヒット含む）ごとに、対象HPを「現在HP − 最大HPの1%（最小1、最大40）」へセットヘルスで直接削ります。
4. **Healing Attack / ヒーリングアタック**: Melee hits restore 3 Mana; spell hits restore 1 HP. / 近接ヒット時マナ3回復、魔法ヒット時HP1回復。
5. **Overload Overclocking / オーバーロードオーバークロッキング**: No cooldown/mana/cast time. Resets all spell cooldowns and restores full mana while applying/stacking the neutral `Overload` effect (-10% Max Mana per level, immune to purification). / クールダウン・マナ・詠唱時間なし。全クールダウンをリセットしマナを全回復する代わりに、浄化不可の中立エフェクト「オーバーロード」（レベル×10%最大マナ減少）を付与・累積。
6. **Vitality Recovery / バイタリティリカバリー**: Restores 5 hunger, 5 saturation, 10 HP (scaled by spell power), and cleanses harmful debuffs. / 満腹度5・隠し満腹度5・HP10（魔法威力依存）を回復し、デバフを浄化。
7. **Sustained Purification / 持続的浄化**: Grants `Purification` for 15 seconds, continuously clearing harmful effects. / 15秒間デバフを消去し続ける「ピュリフィケーション」効果を付与。
8. **Overheel / オーバーヒール**: Full heal + grants `OverHP` attribute (10 * spell power, cleared on death). Missing HP is immediately healed by consuming `OverHP`. / HPを全回復し、`オーバーHP` 属性（10×魔法威力、死亡時消滅）を付与。HPが減ると `オーバーHP` を消費して即座に回復。
9. **Evasive Movement / イヴェイシブムーブメント**: Teleports 10 blocks forward along sight and grants 1 hit of non-teleporting Evasion (`Evasive Dodge`) for 5 seconds. / 視線方向へ10ブロックテレポートし、5秒間のテレポートしない回避バフ（`イヴェイシブドッジ`、1回分）を付与。
10. **Akashic Record: Access / アカシックレコード:アクセス**: 400 Mana, 5s cast, no cooldown (permanently locked at Lv.1). Opens an ender-chest-style 4-page storage (`6×3` slots per page) for ISS spell scrolls. / 消費マナ400、詠唱5秒、クールダウンなし（レベル1固定）。エンダーチェスト方式で管理される全4ページ（各6×3スロット）のスクロール保管庫「アカシックレコード」を開きます。
11–14. **Akashic Record: Execution I–IV / アカシックレコード:実行1〜4**: Permanently locked at Lv.1. Simultaneously casts all scrolls stored on Pages 1–4 at the end of casting. Combined mana (`250 + sum`), cast time (`sum`), cooldown (`sum`), and each spell's level/original school power scaling are read directly from the stored scroll NBT. / レベル1固定。ページ1〜4に格納したスクロールの効果を詠唱終了時にすべて同時に発動します。消費マナ（`250 + 合算`）・詠唱時間（`合算`）・クールダウン（`合算`）・および各呪文のレベルや本来の属性威力参照は格納スクロールのNBTをそのまま引き継ぎます。
15. **Extension / 延長**: 200 Mana, 2s cast, 2m cooldown (supports addon max-level extension). Extends the duration of all active beneficial buffs by 1m30s at the same amplifier (scales with Spell Power and spell level). / 消費マナ200、詠唱2秒、クールダウン2分（アドオンによるレベル上限拡張に対応）。自身の持つすべてのバフを同じ効果レベルのまま1分30秒延長します（魔法威力・呪文レベルで延長時間が増加）。

---

### Akashic Record Notes / アカシックレコード使用上の注意

- **Processing Order / 処理順**: Slots in each page (`6×3`) are processed **from top-left to bottom-right** (left-to-right, top row to bottom row).  
  各ページ（`6×3`）に格納されたスクロールは **左上から右下へ向かって順に** 処理されます。
- **Destructible Projectiles / 破壊されうる弾幕・召喚物について**: Even when spells are invoked simultaneously, **destructible projectiles or entities that break upon taking damage (such as ISS's default Wisps)** may be hit and destroyed by the attack hitboxes of other spells fired in the same execution, causing them to effectively fizzle.  
  同時発動自体は正常に行われていても、**攻撃を受けると破壊されるタイプの弾幕・召喚物（ISS デフォルトのウィスプ等）は、同時に発射した他の弾幕・呪文の攻撃判定によって破壊され、実質不発になることがあります。** 格納する呪文の順番（左上から順に処理される点）や組み合わせにご注意ください。
- **Non-storable Spells / 格納不可の呪文**: Akashic Record spells, Recast/ammunition-style spells (`getRecastCount > 0`), and Continuous-channel spells (`CastType.CONTINUOUS`) cannot be stored in the Akashic Record.  
  一部呪文（アカシックレコード系、リキャスト/弾薬系、継続照射系）はアカシックレコードに格納できません。

---

## License / ライセンス
Dual-licensed under **GPL-3.0-or-later AND LGPL-2.1-or-later**. See [LICENSE](LICENSE).  
**GPL-3.0-or-later AND LGPL-2.1-or-later** のデュアルライセンスです。詳細は [LICENSE](LICENSE) を参照してください。
