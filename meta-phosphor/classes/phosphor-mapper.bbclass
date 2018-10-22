# Common code for recipes that modify the phosphor-mapper
# configuration.
#
# Typically it is not desired to monitor all dbus traffic
# on a system and as such, the phosphor-mapper has command
# line options to control what path namespaces and dbus
# interfaces it will keep track of.
#
# The Phosphor layer by default configures the mapper to
# watch xyz.openbmc_project interfaces and paths only.  This
# configuration file is intended to be inherited by
# native recipes in other layers that wish to add namespaces
# or interfaces to the mapper watchlist.

# Add path namespaces to be monitored:
# PHOSPHOR_MAPPER_NAMESPACE_append = " /foo/bar"

# Add interfaces to be monitored:
# PHOSPHOR_MAPPER_INTERFACE_append = " foo.bar"

inherit phosphor-mapperdir
inherit obmc-phosphor-utils

python phosphor_mapper_do_postinst() {
    def process_var(d, var, dir):
        for p in listvar_to_list(d, var):
            parent = d.getVar('D', True) + d.getVar(dir, True)
            if not os.path.exists(parent):
                os.makedirs(parent)
            path = os.path.join(
                parent,
                '-'.join(p.split(os.sep)))
            with open(path, 'w+') as fd:
                pass

    process_var(d, 'PHOSPHOR_MAPPER_NAMESPACE', 'namespace_dir')
    process_var(d, 'PHOSPHOR_MAPPER_INTERFACE', 'interface_dir')
}

do_install[vardeps] += "PHOSPHOR_MAPPER_NAMESPACE"
do_install[vardeps] += "PHOSPHOR_MAPPER_INTERFACE"
do_install[postfuncs] += "phosphor_mapper_do_postinst"
