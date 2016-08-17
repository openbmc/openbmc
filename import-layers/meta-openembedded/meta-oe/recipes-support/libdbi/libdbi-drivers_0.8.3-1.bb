require ${PN}.inc


SRC_URI[md5sum] = "4de79b323162a5a7652b65b608eca6cd"
SRC_URI[sha256sum] = "4ab9944398ce769c0deeb64d2f73555c67bc25ccd2ade1ccf552226c7b2acf72"

# libdbi-drivers-0.8.3-1/drivers/mysql/dbd_mysql.c:232:5: error: conflicting types for 'dbd_goto_row'
PNBLACKLIST[libdbi-drivers] ?= "BROKEN: fails to build with gcc-5"
