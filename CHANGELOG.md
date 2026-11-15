# Changelog / 変更履歴

## [1.0.3-alpha] - 2026-11-15

### Fixed / 修正
- **Pure Scroll Forge Renderer / ピュアスクロールフォージの描画修正**:
  - Eliminated Z-fighting flickering on the enchanted glint overlay by applying a subtle `1.003x` offset around the block center. / ブロック中心を基準に `1.003` 倍の微小オフセットを加え、移動時のエンチャント光沢のZファイティング（チカチカ）を解消。
  - Aligned placed ink item rotation and position (`INK_POS` + flat table orientation) with Iron's Spells 'n Spellbooks' `ScrollForgeRenderer` so ink no longer clips into the table. / 設置されたインクの座標・回転を本家 `ScrollForgeRenderer` と一致させ、インクがテーブルに刺さらないよう修正。

## [1.0.2-alpha] - 2026-11-15

### Added / 追加
- **Pure School Scroll Texture & Model / 無垢学校のスクロールテクスチャ・モデル**: Added custom 16x16 pastel-iridescent white scroll texture (`scroll_pure.png`) with black runes and registered `item/scroll_pure` model via `ModelEvent.RegisterAdditional`. / 白基調・黒文字・パステル虹色グラデーションの無垢学校スクロール用テクスチャ (`scroll_pure.png`) およびモデル (`item/scroll_pure`) を追加・登録。

### Changed / 変更
- **Japanese Localization Exceptions / 日本語表記の調整**: Updated school/attribute name to `無垢` (`無垢魔法威力` / `無垢魔法耐性`), `soul_shaving` to `魂削り`, and `sustained_purification` to `持続的浄化` in `ja_jp.json`. / `ja_jp.json` にて学校・属性名を「無垢」（「無垢魔法威力」「無垢魔法耐性」）、`soul_shaving` を「魂削り」、`sustained_purification` を「持続的浄化」に調整。

## [1.0.1-alpha] - 2026-11-15

### Changed / 変更
- **Pure Scroll Forge UI & Tooltips / ピュアスクロールフォージUI・ツールチップ**: Matched card rendering, runic font for insufficient ink, and hover spell description tooltips (`TooltipsUtils.createSpellDescriptionTooltip`) to Iron's Spells 'n Spellbooks' vanilla Scroll Forge. / カード描画、インク不足時のルーン文字表示、マウスホバー時の呪文説明ツールチップを通常のスクロールフォージと同一の仕様に変更。
- **Single-Rarity Scroll Crafting / 単一レアリティ呪文の上位インク作成対応**: Allowed single-rarity and max-level-1 spells (such as Pocket Dimension or Pure spells) to be forged using any ink at or above their minimum rarity (e.g. Legendary Ink). / レアリティが1種類のみの呪文や最大レベル1の呪文を、最小レアリティ以上の上位インク（レジェンダリーインク等）を消費して作成できるように修正。
- **Japanese Localization / 日本語ローカライズ拡充**: Localized all Pure spell names, effect names, attributes, creative tab, and guide descriptions in `ja_jp.json` using katakana loanwords. / `ja_jp.json` 内のすべての呪文名・エフェクト名・属性名・クリエイティブタブ・説明文を日本語（カタカナ表記）へ完全対応。

## [1.0.0-alpha] - 2026-11-15

### Added / 追加
- **Pure Scroll Forge / 純粋なスクロールフォージ**: Ink-only scroll forge capable of crafting any registered spell scroll (including treasure-only and Pure spells). / インクのみで動作し、お宝専用・Pure呪文を含むすべての登録済み呪文スクロールを作成可能なスクロールフォージを追加。
- **Pure Spellbook / 純粋な魔導書**: 12-slot spellbook with whitened Apprentice Spellbook aesthetic and enchanted glint. / 白を基調とした表紙にエンチャント光沢を持つ12スロット魔導書を追加。
- **Pure School & 9 Spells / Pure属性と9種の呪文**:
  - `empowered`, `weapon_interaction`, `soul_shaving`, `healing_attack`, `overload_overclocking`, `vitality_recovery`, `sustained_purification`, `overheel`, `evasive_movement`
- **Mob Effects & Attribute / 状態異常・属性**:
  - `empowered`, `weapon_interaction`, `soul_shaving`, `healing_attack`, `overload`, `purification`
  - `over_hp` attribute (`pure_spellbook_and_things:over_hp`)
- **Loot & Localization / ルートテーブル・多言語対応**:
  - Global loot modifier for Pure spell scrolls in chests (`en_us.json`, `ja_jp.json`). / チェスト等のルートテーブルへの Pure スクロール追加、日英両言語のローカライズ実装。
