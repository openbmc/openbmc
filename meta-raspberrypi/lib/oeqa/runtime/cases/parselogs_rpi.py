from oeqa.runtime.cases.parselogs import *

rpi_errors = [
]

ignore_errors['raspberrypi4'] = rpi_errors + common_errors
ignore_errors['raspberrypi4-64'] = rpi_errors + common_errors
ignore_errors['raspberrypi3'] = rpi_errors + common_errors
ignore_errors['raspberrypi3-64'] = rpi_errors + common_errors

class ParseLogsTestRpi(ParseLogsTest):
    pass
