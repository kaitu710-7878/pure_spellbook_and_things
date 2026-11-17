# Changelog / 変更履歴

## [1.1.2-beta] - 2026-11-17

### Fixed / 修正
- **Target & Recast Spell Stability / ターゲット魔法の安定化と弾薬（リキャスト）魔法の格納制限**:
  - Added pre-cast target/cast-data snapshotting in `AkashicRecordExecSpell.checkPreCastConditions` with fallback restoration during `onCast`, ensuring targeting spells execute reliably even if sight shifts or earlier spells defeat a target. / 詠唱開始時に各呪文のターゲット・プレキャスト情報をスナップショット保存し、`onCast` 時に視線外れ等があっても安定して発動するよう改善。
  - Blocked Recast/ammunition-style spells (`getRecastCount > 0`, including `Portal`, `Flaming Barrage`, `Eldritch Blast`, etc.) from being stored in or invoked by the Akashic Record, and wrapped per-spell execution in `try/catch` to prevent server crashes such as `PortalManager.killPortal` NPE. / `Portal` や `Flaming Barrage` 等のリキャスト（弾薬）系呪文をアカシックレコードに格納不可とし、各呪文実行時の例外保護により `PortalManager.killPortal` 等のクラッシュを防止。

## [1.1.1-beta] - 2026-11-17

### Fixed / 修正
- **Akashic Record Recursion Prevention / アカシックレコード無限ループ防止**: Blocked Akashic Record spells (`akashic_record_access` and `akashic_record_exec_1..4`) from being stored in or read by Akashic Record slots in `AkashicRecordHelper.isValidScroll`, preventing recursive cost calculation and execution. / `AkashicRecordHelper.isValidScroll` にてアカシックレコード系呪文のスクロールを格納・参照不可とし、無限ループおよび不正なコスト合算を防止。

## [1.1.0-beta] - 2026-11-17

### Added / 追加
- **Pure Rune & Pure Upgrade Orb / 無垢のルーン・無垢のオーブ**:
  - Added `pure_rune` (crafted from 1 Blank Rune + 6 Arcane Essence + 2 Cinder Essence) with enchanted glint. / ブランク・ルーン×1＋秘術のエッセンス×6＋燃え殻のエッセンス×2でクラフトできる「無垢のルーン」を追加。
  - Added `pure_upgrade_orb` (crafted from 8 Pure Runes + 1 Upgrade Orb) granting `+5%` Pure Spell Power (`pure_power` UpgradeOrbType) to armor and spellbooks. / 無垢のルーン×8＋空白のオーブでクラフトし、防具・スペルブックに無垢魔法威力+5%を付与できる「無垢のオーブ」を追加。
- **Akashic Record System & Spells / アカシックレコード機能・呪文 (5 Spells)**:
  - `akashic_record_access`: Opens an ender-chest-style 4-page (6x3 slots per page) storage for any ISS scroll (400 mana, 5s cast, no cooldown, permanently locked to Lv.1). / 4ページ（各6×3）のエンダーチェスト方式スクロール保管庫を開く「アカシックレコード:アクセス」を追加。
  - `akashic_record_exec_1` .. `4`: Simultaneously executes all scrolls stored in Pages 1–4 using combined mana (250 + sum), combined cast time, and combined cooldown while preserving each scroll's own NBT level and original school power scaling (permanently locked to Lv.1). / 各ページの格納スクロールを合算マナ・合算詠唱時間・合算クールダウンで同時発動する「アカシックレコード:実行1〜4」を追加。
- **Extension Spell / 延長呪文 (`extension`)**:
  - Extends all active beneficial buffs by 1m30s at their current amplifier (200 mana, 2s cast, 120s cooldown; scales with Spell Power and addon-extended levels). / 自身の持つすべてのバフを同じ効果レベルのまま1分30秒延長する「延長」を追加（魔法威力・アドオンによるレベル上限拡張に対応）。

## [1.0.6-beta] - 2026-11-15

### Changed / 変更
- Promoted release stage from Alpha to Beta (`1.0.6-beta`) following in-game verification. / 実機での動作確認を完了し、リリース段階を Alpha から Beta（`1.0.6-beta`）へ移行。

## [1.0.5-alpha] - 2026-11-15

### Added / 追加
- **Non-Teleporting Evasion Effect / テレポート無し回避エフェクト (`evasive_dodge`)**: Added `EvasiveDodgeEffect` using the same damage-negation logic as ISS's `EvasionEffect` without random teleportation, applied for 5 seconds by `Evasive Movement`. / ダメージ・飛翔体回避ロジックを本家 `EvasionEffect` と同等に保ちつつランダムテレポートを発生させない専用エフェクト `evasive_dodge`（5秒）を追加し、`Evasive Movement` から付与するよう変更。

### Fixed / 修正
- **Pure Scroll Forge Glint Rendering / ピュアスクロールフォージの光沢描画修正**: Extracted `POSITION_TEX` vertices directly from `BakedQuad`s in `PureScrollForgeRenderer` so `PureRenderTypes.blockGlint()` renders cleanly without vertex format mismatch. / `PureScrollForgeRenderer` にて `BakedQuad` から直接 `POSITION_TEX` 頂点を出力するように修正し、頂点フォーマット不一致によるエンチャント光沢の非表示を解消。
- **Creative Tab Name / クリエイティブタブ名**: Kept the creative tab name in English (`Pure Spellbook and things`) in `ja_jp.json`. / `ja_jp.json` のクリエイティブタブ名を英語表記（`Pure Spellbook and things`）に戻しました。

## [1.0.4-alpha] - 2026-11-15

### Fixed / 修正
- **Pure Scroll Forge Glint & Ink Facing / ピュアスクロールフォージの光沢表示とインクの向き修正**:
  - Replaced `RenderType.glint()` (which uses `EQUAL_DEPTH_TEST` and fails when scaled) with a custom `PureRenderTypes.blockGlint()` using `LEQUAL_DEPTH_TEST` and `POLYGON_OFFSET_LAYERING` so the enchanted glint renders reliably without flickering. / `EQUAL_DEPTH_TEST` によりスケール変更時に消えていた `RenderType.glint()` を廃止し、`LEQUAL_DEPTH_TEST` + `POLYGON_OFFSET_LAYERING` を用いたカスタム `PureRenderTypes.blockGlint()` を導入してチカチカせず確実にエンチャント光沢が表示されるよう修正。
  - Fixed `getBlockFacingDegrees` direction mapping (`NORTH -> 180`, `EAST -> 90`, `WEST -> -90`, `SOUTH -> 0`) to match Iron's Spells 'n Spellbooks' `ScrollForgeRenderer`. / `getBlockFacingDegrees` の方角マッピングを本家 `ScrollForgeRenderer` と一致させ、同じ向きに設置した際インクが反対側に描画される不具合を修正。

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
