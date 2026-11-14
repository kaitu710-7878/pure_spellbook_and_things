# Pure Spellbook and things

An addon mod for **Iron's Spells 'n Spellbooks** (Minecraft 1.20.1 Forge) introducing the **Pure** magic school, **Pure Scroll Forge**, **Pure Spellbook**, and 9 unique Legendary Pure spells.

**Iron's Spells 'n Spellbooks**（Minecraft 1.20.1 Forge）向けのアドオンMODです。**Pure（純粋）**属性の魔法、**純粋なスクロールフォージ**、**純粋な魔導書**、および9種類のユニークなレジェンダリー呪文を追加します。

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
- **Pure Scroll Forge / 純粋なスクロールフォージ**
  - Crafted with `Scroll Forge` + 8 `Arcane Essence`. / スクロールフォージ + アルカナエッセンス×8 でクラフト。
  - Operates using **Ink only** (paper and focus slots are visual-only). / **インクのみ**で動作します（紙と属性素材スロットは見た目のみ）。
  - Ignores normal crafting restrictions, allowing treasure-only and Pure scrolls to be forged. / 通常のクラフト可否フラグを無視し、お宝専用スクロールや Pure 呪文も作成可能です。
- **Pure Spellbook / 純粋な魔導書**
  - Crafted with `Book` + 8 `Arcane Essence`. / 本 + アルカナエッセンス×8 でクラフト。
  - Simple 12-slot spellbook with no special equipment stat bonuses. / 装備時効果を持たないシンプルな12スロット魔導書。

### Pure School & Spells / Pure属性と呪文 (9 Spells)
All Pure spells are **Legendary (Max Level 1)**, cannot be crafted in a normal Scroll Forge, cannot be cast by enemies, and can be obtained via Pure Scroll Forge, loot tables, or Creative mode.

すべての Pure 呪文は**レジェンダリー（通常最大レベル1）**で、通常のスクロールフォージでは作成できず、敵も詠唱しません。純粋なスクロールフォージ、チェスト等のルートテーブル、クリエイティブモードから入手できます。

1. **Empowered / エンパワード**: +40% Spell Power, +85% Attack Damage, and uninterruptible casting. / 魔法威力+40%、近接攻撃力+85%、詠唱中断無効を付与。
2. **Weapon Interaction / ウェポンインタラクション**: Adds held weapon base damage to all spell damage. / 手に持つ武器の基礎攻撃力をすべての魔法ダメージに加算。
3. **Soul Shaving / ソウルシェービング**: Every hit (including during i-frames and multi-hit spells) reduces target HP directly by `1% of Max HP` (clamped 1–40) via `setHealth`. / すべての攻撃ヒット（無敵時間中や多段ヒット含む）ごとに、対象HPを「現在HP − 最大HPの1%（最小1、最大40）」へ直接上書き。
4. **Healing Attack / ヒーリングアタック**: Melee hits restore 3 Mana; spell hits restore 1 HP. / 近接ヒット時マナ3回復、魔法ヒット時HP1回復。
5. **Overload Overclocking / オーバーロードオーバークロッキング**: No cooldown/mana/cast time. Resets all spell cooldowns and restores full mana while applying/stacking the neutral `Overload` effect (-10% Max Mana per level, immune to purification). / クールダウン・マナ・詠唱時間なし。全クールダウンをリセットしマナを全回復する代わりに、浄化不可の中立エフェクト「Overload」（レベル×10%最大マナ減少）を付与・累積。
6. **Vitality Recovery / バイタリティリカバリー**: Restores 5 hunger, 5 saturation, 10 HP (scaled by spell power), and cleanses harmful debuffs. / 満腹度5・隠し満腹度5・HP10（魔法威力依存）を回復し、デバフを浄化。
7. **Sustained Purification / サステインド・ピュリフィケーション**: Grants `Purification` for 15 seconds, continuously clearing harmful effects. / 15秒間デバフを消去し続ける「Purification」効果を付与。
8. **Overheel / オーバーヒール**: Full heal + grants `OverHP` attribute (10 * spell power, cleared on death). Missing HP is immediately healed by consuming `OverHP`. / HPを全回復し、`OverHP` 属性（10×魔法威力、死亡時消滅）を付与。HPが減ると `OverHP` を消費して即座に回復。
9. **Evasive Movement / イヴェイシブ・ムーブメント**: Teleports 10 blocks forward along sight and grants 1 hit of Evasion for 5 seconds. / 視線方向へ10ブロックテレポートし、5秒間の回避（1回分）を付与。

---

## License / ライセンス
Dual-licensed under **GPL-3.0-or-later AND LGPL-2.1-or-later**. See [LICENSE](LICENSE).  
**GPL-3.0-or-later AND LGPL-2.1-or-later** のデュアルライセンスです。詳細は [LICENSE](LICENSE) を参照してください。
