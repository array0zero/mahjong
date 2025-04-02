from mahjong.hand_calculating.hand import HandCalculator
from mahjong.tile import TilesConverter
from mahjong.hand_calculating.hand_config import HandConfig, OptionalRules
from mahjong.meld import Meld
from mahjong.constants import EAST, SOUTH, WEST, NORTH

# HandCalculator(計算用クラス)のインスタンスを生成
calculator = HandCalculator()

# 結果出力用
def print_hand_result(hand_result):
    results = {
        'han': hand_result.han,
        'fu': hand_result.fu,
        'cost_main': hand_result.cost['main'],
        'cost_additional': hand_result.cost['additional'],
        'yaku': hand_result.yaku,
        'fu_details': [str(fu_item) for fu_item in hand_result.fu_details]
    }
    return results

# アガリ形(man=マンズ, pin=ピンズ, sou=ソーズ, honors=字牌,赤ドラの場合は0, has_aka_dora=Trueに変更)
tiles = TilesConverter.string_to_136_array(man='11330066', pin='224466', sou='', has_aka_dora=True)

# アガリ牌(ソーズの5)
win_tile = TilesConverter.string_to_136_array(man='1')[0]

# 鳴き(なし)
melds = None

#鳴き（あり）
#鳴き(チー:CHI, ポン:PON, カン:KAN(True:ミンカン,False:アンカン), カカン:CHANKAN, ヌキドラ:NUKI)
# melds = [
#     Meld(Meld.KAN, TilesConverter.string_to_136_array(man='2222'), False),
#     Meld(Meld.PON, TilesConverter.string_to_136_array(pin='333')),
#     Meld(Meld.CHI, TilesConverter.string_to_136_array(sou='567'))
# ]


# ドラ(なし)
dora_indicators = None

#ドラがある場合
# dora_indicators = [
#     TilesConverter.string_to_136_array(pin='7')[0],
#     TilesConverter.string_to_136_array(sou='9')[0],
# ]

# オプション(なし)
config = HandConfig(is_riichi=True, player_wind=EAST, round_wind=EAST,options=OptionalRules(has_aka_dora=True))

# オプション（あり）
#オプション(リーチ, 自風, 場風)
#config = HandConfig(is_riichi=True, player_wind=EAST, round_wind=EAST)

# 計算
result = calculator.estimate_hand_value(tiles, win_tile, melds, dora_indicators, config)

result_data = print_hand_result(result)
result_data