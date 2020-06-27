inherit esw

ESW_COMPONENT_SRC = "/lib/sw_services/xilpm/src/"
ESW_COMPONENT_NAME = "libxilpm.a"

DEPENDS = "libxil"
DEPENDS_microblaze-plm_append = "xilplmi"
