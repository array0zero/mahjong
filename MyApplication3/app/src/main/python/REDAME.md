〜mahjongライブラリの使い方〜
・tiles(麻雀牌のアガリ形)
・win_tile(アガリ牌)
・melds(鳴き)
・dora_indicators(ドラ)
・config(オプション)  を入力すると点数計算をしてくれる。

Titles(アガリ形) - tiles = TilesConverter.string_to_136_array(man='??', pin='??', sou='??', honors='??')
・man,pin,sou=　1から9, 0は赤ドラ　使用する際は　"has_aka_dora=True"を記述
・honors=1:東, 2:南, 3:西, 4:北, 5:白, 6:發, 7:中)

Win_title(アガリ牌)　- win_title = TilesConverter.string_to_136_array(man='??', pin='??', sou='??', honors='??')[0]

Melds(鳴き) - melds = ???
·なし         →　melds = None
·ポン         → Meld(Meld.PON, TilesConverter.string_to_136_array(man='?',pin='?',sou='?',honors='?'))
·チー         → Meld(Meld.CHO, TilesConverter.string_to_136_array(man='?',pin='?',sou='?',honors='?'))
·アンカン      → Meld(Meld.KAN, TilesConverter.string_to_136_array(??='????'), False)
·ダイミンカン   → Meld(Meld.KAN, TilesConverter.string_to_136_array(??='????'), True)

Dora_indicators(ドラ) - dora_indicators = TilesConverter.string_to_136_array(??='?')[0]　（表示牌を枚数分だけ）


Config（オプション） - config = HandConfig(?????? = true)
・リーチ　　　　　　　→　is_riichi
・イッパツ　　　　　　→　is_ippatsu
・リンシャンカイホウ　→　is_rinshan
・チャンカン　　　　　→　is_chankan
・ハイテイ　　　　　　→　is_haitei
・ホウテイ　　　　　　→　is_houtei
・ダブルリーチ　　　　→　is_daburu_riichi
・流しマンガン　　　　→　is_nagashi_mangan
・テンホー　　　　　　→　is_tenhou
・レンホー　　　　　　→　is_renhou
・チーホー　　　　　　→　is_chiihou
・場風             →  round_wind=EAST or SOUTH or EAST orNORTH
・自風             → player_wind= ???


その他のオプションルール - config = HandConfig(options=OptionalRules(???))
·赤ドラルール              → has_aka_dora(T or F)
·タンヤオ（喰いタン）       →　has_open_tanyao(T or F)
・ダブルヤクマン　　　　　　　→　has_double_yakuman(T or F)
・数えヤクマン　　　　　　　　→　kazoe_limit(kazoe_limit = HandConfig.KAZOE_LIMITED,HandConfig.KAZOE_SANBAIMAN,HandConfig.KAZOE_NO_LIMIT)
・切り上げマンガン　　　　　　→　kiriage(T or F)
・ピンフ　　　　　　　　　　　→　fu_for_open_pinfu(T or F)
・ピンフツモ　　　　　　　　　→　fu_for_pinfu_tsumo(T or F)
・レンホー　　　　　　　　　　→　renhou_as_yakuman(T or F)
・ダイシャリン　　　　　　　　→　has_daisharin(T or F)
・ダイチクリン&ダイスウリン　 →　has_daisharin_other_suits(T or F)

役の一覧

"Riichi": "立直(リーチ)",
"Ippatsu": "一発(イッパツ)",
"Tanyao": "断么九(タンヤオ)",
"Pinfu": "平和(ピンフ)",
"Iipeiko" : "一盃口(イーペイコー)",
"Ryanpeikou" : "二盃口(リャンペイコー)",
"ChiiToitsu": "七対子(チートイツ)",
"Ittsu" : "一気通貫(イッツー)",
"San Ankou": "三暗刻(サンアンコウ)",
"ToiToi": "対々和(トイトイ)",
"Sanshoku Doujun": "三色同順",
"Sanshoku Doukou" : "三色同刻",
"Chantai" : "チャンタ",
"Junchan" : "ジュンチャン",
"Honitsu": "混一色（ホンイツ）",
"Chinitsu": "清一色(チンイツ) ",
"Shou Sangen" : "小三元",
"Daisangen" : "大三元",
"Ryuuiisou" : "緑一色(リューイーソー)",
"Shousuushii" : "小四喜(ショウスーシー)",
"Dai Suushii" : "大四喜(ダイスーシー)",
"Tsuu Iisou" : "字一色(ツーイーソー)",
"Suu Ankou" : "四暗刻",
"Suu Ankou Tanki" : "四暗刻単騎",
"Chuuren Poutou" : "九蓮宝燈(チューレンポートウ)",
"Chinroutou" : "清老頭(チンロウトウ)",
"Yakuhai (haku)" : "白",
"Yakuhai　(hatsu)" : "發",
"Yakuhai (chun)" : "中",
"Dora": "ドラ",
"UraDora": "裏ドラ",
"Renhou": "人和",
"Chiihou": "地和",
"Tenhou": "天和",
"Haitei": "海底撈月",
"Rinshan": "嶺上開花",
"Chankan": "槍槓",
"NagashiMangan": "流し満貫",
"DoubleRiichi": "ダブル立直",
"NoTen": "ノーテン"