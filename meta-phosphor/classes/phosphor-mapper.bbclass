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
# configuration file is intented to be inherited by
# native recipes in other layers that wish to add namespaces
# or interfaces to the mapper watchlist.

# Add path namespaces to be monitored:
# PHOSPHOR_MAPPER_NAMESPACE_append = " /foo/bar"

# Add interfaces to be monitored:
# PHOSPHOR_MAPPER_INTERFACE_append = " foo.bar"

# Blacklist paths from being monitored:
# PHOSPHOR_MAPPER_BLACKLIST_append = " /foo/bar/baz"

# Blacklist interfaces from being monitored:
# PHOSPHOR_MAPPER_INTERFACE_BLACKLIST_append = " foo.bar.baz"

inherit phosphor-mapperdir

python phosphor_mapper_do_postinst() {
    for p in listvar_to_list(d, 'PHOSPHOR_MAPPER_NAMESPACE'):
        parent = d.getVar('D', True) + d.getVar('namespace_dir', True)
        if not os.path.exists(parent):
            os.makedirs(parent)
        path = os.path.join(
            parent,
            '-'.join(p[1:].split(os.sep)))
        with open(path, 'w+') as fd:
            pass
    for i in listvar_to_list(d, 'PHOSPHOR_MAPPER_INTERFACE'):
        parent = d.getVar('D', True) + d.getVar('interface_dir', True)
        if not os.path.exists(parent):
            os.makedirs(parent)
        path = os.path.join(
            parent,
            '-'.join(i.split('.')))
        with open(path, 'w+') as fd:
            pass
    for b in listvar_to_list(d, 'PHOSPHOR_MAPPER_BLACKLIST'):
        parent = d.getVar('D', True) + d.getVar('blacklist_dir', True)
        if not os.path.exists(parent):
            os.makedirs(parent)
        path = os.path.join(
            parent,
            '-'.join(b[1:].split(os.sep)))
        with open(path, 'w+') as fd:
            pass
    for ib in listvar_to_list(d, 'PHOSPHOR_MAPPER_INTERFACE_BLACKLIST'):
        parent = d.getVar('D', True) + d.getVar('interfaceblacklist_dir', True)
        if not os.path.exists(parent):
            os.makedirs(parent)
        path = os.path.join(
            parent,
            '-'.join(ib.split('.')))
        with open(path, 'w+') as fd:
            pass
}

do_install[vardeps] += "PHOSPHOR_MAPPER_NAMESPACE"
do_install[vardeps] += "PHOSPHOR_MAPPER_INTERFACE"
do_install[vardeps] += "PHOSPHOR_MAPPER_BLACKLIST"
do_install[vardeps] += "PHOSPHOR_MAPPER_INTERFACE_BLACKLIST"
do_install[postfuncs] += "phosphor_mapper_do_postinst"
