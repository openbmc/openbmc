PACKAGECONFIG:remove = "${@bb.utils.contains('TCMODE', 'external-arm', 'libmount-mountfd-support', '' , d)}"
