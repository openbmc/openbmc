fdt addr ${fdt_addr} && fdt get value bootargs /chosen bootargs
fatload mmc 0:1 ${kernel_addr_r} uImage
bootm ${kernel_addr_r} - ${fdt_addr}
