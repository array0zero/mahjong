import json
from mahjong.hand_calculating.hand import HandCalculator
from mahjong.tile import TilesConverter
from mahjong.hand_calculating.hand_config import HandConfig
from mahjong.constants import EAST

# HandCalculator(計算用クラス)のインスタンスを生成
calculator = HandCalculator()

# 役名の日本語マッピング
yaku_translation = {
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

}

def calculate_score(input_data):
    try:
        # JSONデータをパース
        data = json.loads(input_data)
        man = data.get("man", "")
        pin = data.get("pin", "")
        sou = data.get("sou", "")
        honors = data.get("honors", "")

        # 牌の設定
        tiles = TilesConverter.string_to_136_array(man=man, pin=pin, sou=sou, honors=honors)

        # 上がり牌の設定
        win_tile_data = data.get("win_tile")
        if not win_tile_data:
            return json.dumps({"error": "上がり牌が指定されていません。"})

        win_tile_type = win_tile_data.get("type")
        win_tile_value = str(win_tile_data.get("value", ""))
        win_tile = TilesConverter.string_to_136_array(**{win_tile_type: win_tile_value})[0]

        # ドラ表示牌の設定
        dora_data = data.get("dora")
        dora_indicators = []
        if dora_data:
            dora_type = dora_data.get("type")
            dora_value = str(dora_data.get("value", ""))
            dora_indicators = [TilesConverter.string_to_136_array(**{dora_type: dora_value})[0]]

        # 設定
        config = HandConfig(is_riichi=True, player_wind=EAST, round_wind=EAST)

        # 計算
        result = calculator.estimate_hand_value(tiles, win_tile, None, dora_indicators, config)
        if not result:
            return json.dumps({"error": "アガリ形が無効です。"})

        # 結果を日本語にフォーマット
        output = {
            "翻 (ハン)": result.han,  # Hanの数を翻（ハン）として出力
            "符 (フ)": result.fu,     # Fuの数を符（フ）として出力
            "基本点": result.cost.get("main", 0),  # 基本点
            #"追加点": result.cost.get("additional", 0),  # 追加点
            "役": [yaku_translation.get(str(y.name), str(y.name)) for y in result.yaku],  # 役名を日本語に変換して出力
            "符の詳細": [str(fu) for fu in result.fu_details]  # 符の詳細を日本語で出力
        }
        return json.dumps(output, ensure_ascii=False)

    except Exception as e:
        return json.dumps({"error": f"エラーが発生しました: {str(e)}"}, ensure_ascii=False)
