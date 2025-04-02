from mahjong.hand_calculating.hand import HandCalculator
from mahjong.tile import TilesConverter
from mahjong.hand_calculating.hand_config import HandConfig, OptionalRules
from mahjong.constants import EAST, SOUTH

# HandCalculator(計算用クラス)のインスタンスを生成
calculator = HandCalculator()

# アガリ形（例: マンズとピンズ）
tiles = TilesConverter.string_to_136_array(man='22440077', pin='223344', sou='',has_aka_dora=True)

# アガリ牌（例: ピンズの2）
win_tile = TilesConverter.string_to_136_array(pin='2')[0]

# 鳴き（なし）
melds = None

# ドラ表示牌（例: ピンズの7）
dora_indicators = [TilesConverter.string_to_136_array(pin='4')[0]]

# オプション（リーチ、東場、東家）
config = HandConfig(is_riichi=True, player_wind=EAST, round_wind=EAST, options=OptionalRules(has_aka_dora=True))

# 計算
result = calculator.estimate_hand_value(tiles, win_tile, melds, dora_indicators, config)

# 結果出力用関数
def print_hand_result(hand_result):
    if not hand_result:
        return {"error": "計算結果が存在しません。入力データを確認してください。"}
    return {
        'han': hand_result.han,
        'fu': hand_result.fu,
        'cost_main': hand_result.cost.get('main', 0),
        'cost_additional': hand_result.cost.get('additional', 0),
        'yaku': [str(y.name) for y in hand_result.yaku],
        'fu_details': [str(fu_item) for fu_item in hand_result.fu_details],
    }

# 結果を計算
if result:
    final_result = print_hand_result(result)
else:
    final_result = {"error": "アガリ形が不正です。"}
