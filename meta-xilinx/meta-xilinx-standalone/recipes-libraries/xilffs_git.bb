inherit esw

ESW_COMPONENT_SRC = "/lib/sw_services/xilffs/src/"
ESW_COMPONENT_NAME = "libxilffs.a"

EXTRA_OECMAKE += "-DXILFFS_use_mkfs=OFF"
EXTRA_OECMAKE += "-DXILFFS_read_only=ON"
EXTRA_OECMAKE += "-DXILFFS_word_access=OFF"

DEPENDS += "xilstandalone libxil"
