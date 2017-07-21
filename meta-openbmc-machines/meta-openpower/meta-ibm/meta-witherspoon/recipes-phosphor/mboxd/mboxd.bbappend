MBOXD_FLASH_SIZE = "64M"

EXTRA_OECONF := "${@oe_filter_out('enable_virtual_pnor=no', '', d)}"
