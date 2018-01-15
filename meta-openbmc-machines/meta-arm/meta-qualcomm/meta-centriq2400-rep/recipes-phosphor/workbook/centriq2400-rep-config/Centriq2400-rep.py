## System states
##   state can change to next state in 2 ways:
##   - a process emits a GotoSystemState signal with state name to goto
##   - objects specified in EXIT_STATE_DEPEND have started
SYSTEM_STATES = [
    'BASE_APPS',
    'BMC_STARTING',
    'BMC_READY',
    'HOST_POWERING_ON',
    'HOST_POWERED_ON',
    'HOST_BOOTING',
    'HOST_BOOTED',
    'HOST_POWERED_OFF',
]

EXIT_STATE_DEPEND = {
    'BASE_APPS' : {
        '/org/openbmc/sensors': 0,
    },
    'BMC_STARTING' : {
        '/org/openbmc/control/power0' : 0,
        '/org/openbmc/control/host0' : 0,
        '/org/openbmc/control/chassis0' : 0,
    },
}

FRU_INSTANCES = {
'<inventory_root>/system/chassis/motherboard/bmc' : { 'fru_type' : 'BMC','is_fru' : False, 'manufacturer' : 'ASPEED' },
}
# I believe these numbers need to match the yaml file used to create the c++ ipmi map.
# the devices have types, but I don't believe that factors in here, I think these are
# just unique IDs.
ID_LOOKUP = {
    'FRU' : {},
    # The number at the end needs to match the FRU ID.
    # https://github.com/openbmc/skeleton/blob/master/pysystemmgr/system_manager.py#L143
    # The paramter for it is of type 'y' (unsigned 8-bit integer) presumably decimal?
    'FRU_STR' : {},
    'SENSOR' : {},
    'GPIO_PRESENT' : {}
}

GPIO_CONFIG = {}
GPIO_CONFIG['POWER_BUTTON'] = { 'gpio_pin': 'D3', 'direction': 'out' }
GPIO_CONFIG['PGOOD'] = { 'gpio_pin': 'E2', 'direction': 'in' }
GPIO_CONFIG['BMC_READY'] = { 'gpio_pin': 'Q4', 'direction': 'out' }
GPIO_CONFIG['HOST_SPI_SWITCH'] = { 'gpio_pin': 'C7', 'direction': 'out'}
GPIO_CONFIG['IMC_READY'] = { 'gpio_pin': 'O3', 'direction': 'both' }
GPIO_CONFIG['IMC_INTERRUPT'] = { 'gpio_pin': 'O4', 'direction': 'both' }
GPIO_CONFIG['RESET_BUTTON'] = { 'gpio_pin': 'G5', 'direction': 'both' }
GPIO_CONFIG['QDF_RAS_ERROR_0'] = { 'gpio_pin': 'D6', 'direction': 'in' }
GPIO_CONFIG['QDF_RAS_ERROR_1'] = { 'gpio_pin': 'D7', 'direction': 'in' }
GPIO_CONFIG['QDF_RAS_ERROR_2'] = { 'gpio_pin': 'F1', 'direction': 'in' }

GPIO_CONFIGS = {
        'power_config' : {
        'power_good_in' : 'PGOOD',
        'power_up' : [
#delay in ms
            ('POWER_BUTTON', 'LOW_HIGH',1000),
        ],
        'power_out' : [
            ('POWER_BUTTON', 'LOW_HIGH',8000),
        ],
    }}
        'host_config' : {
        'imc_ready' : 'IMC_READY',
        'imc_interrupt': 'IMC_INTERRUPT',
        'bmc_ready' : 'BMC_READY',
        'host_spi_switch' : 'HOST_SPI_SWITCH',
    },
}

# Miscellaneous non-poll sensor with system specific properties.
# The sensor id is the same as those defined in ID_LOOKUP['SENSOR'].

MISC_SENSORS = {

}

# vim: tabstop=8 expandtab shiftwidth=4 softtabstop=4
