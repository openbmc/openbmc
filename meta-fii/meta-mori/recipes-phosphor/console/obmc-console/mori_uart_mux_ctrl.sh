#!/bin/bash
# Provide source directive to shellcheck.
# shellcheck source=meta-fii/meta-mori/recipes-mori/mori-fw-utility/mori-fw/mori-lib.sh
source /usr/libexec/mori-fw/mori-lib.sh

set_gpio_ctrl S0_UART0_BMC_SEL 1
set_gpio_ctrl S0_UART1_BMC_SEL 1
echo "UART initialization complete"
