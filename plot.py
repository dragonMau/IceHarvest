# float heatToTemp(float heat){
#     float base = 20f;        // ambient
#     float A = 60f;           // scaling factor
#     float B = 2.7f;          // exponential curve
#     return base + Math.signum(heat) * A * Mathf.pow(Math.abs(heat), B);
# }
try:
    from numpy import sign, linspace, abs
    from matplotlib import pyplot as plt
    HAVEIMPORTS = True
except ImportError:
    HAVEIMPORTS = False
HAVEIMPORTS = False

def search(func, target, threshold = 0.0000001):
    guess = 1
    lower_bound = -1
    upper_bound = 1
    result = func(lower_bound)
    while result > target:
        lower_bound *= 2
        result = func(lower_bound)
    result = func(upper_bound)
    while result < target:
        upper_bound *= 2
        result = func(upper_bound)
    guess = (lower_bound + upper_bound) / 2
    result = func(guess)
    while abs(result - target) > threshold:
        if result < target:
            lower_bound = guess
        if result > target:
            upper_bound = guess
        guess = (lower_bound + upper_bound) / 2
        result = func(guess)

    return guess, result


def heat_to_temp(heat):
    return 25 + 200 * heat

hotrock = 0.5
magmarock = 0.75

# print(heat_to_temp(-0.5))
# print(heat_to_temp(-0.25))
# print(heat_to_temp(-0.125))
print(search(heat_to_temp, -100, 0))
# print(heat_to_temp(0))
# # print(search(heat_to_temp, 20, 0.05))
# print(heat_to_temp(hotrock))
# print(heat_to_temp(magmarock))

if HAVEIMPORTS:
    x = linspace(-5, 5, 1000)
    y = heat_to_temp(x)


    plt.plot(x, y)
    plt.show()