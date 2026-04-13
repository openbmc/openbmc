# Rainier has the same schematic on the IO exp for LCD debug card, hence follow
# yosemite5a7 to apply the same configurations as yosemite5.
EXTRA_OEMESON:remove = "-Dmachine='${MACHINE}'"
EXTRA_OEMESON:append = " -Dmachine='yosemite5'"
