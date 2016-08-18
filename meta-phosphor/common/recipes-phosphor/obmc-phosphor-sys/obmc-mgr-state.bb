SUMMARY = "OpenBMC state manager"
DESCRIPTION = "OpenBMC state manager."
PR = "r1"

inherit skeleton-python
inherit obmc-phosphor-systemd

VIRTUAL-RUNTIME_skeleton_workbook ?= ""
STANDBY_OBJECTS ?= ""

RDEPENDS_${PN} += "\
        python-dbus \
        python-json \
        python-subprocess \
        python-pygobject \
        "

SKELETON_DIR = "pystatemgr"
STANDBY_OVERRIDE = "obmc-standby-deps.conf"
SYSTEMD_SERVICE_${PN} += "obmc-mgr-state.service obmc-wait-standby.service"
SYSTEMD_OVERRIDE_${PN} += "${STANDBY_OVERRIDE}:obmc-wait-standby.service.d/${STANDBY_OVERRIDE}"

python add_standby_objects() {
    path = d.getVar('D', True)
    path += '/%s' % d.getVar('systemd_system_unitdir', True)
    path += '/obmc-wait-standby.service.d/'

    if not os.path.exists(path):
        os.makedirs(path)

    path += d.getVar('STANDBY_OVERRIDE', True)

    with open(path, 'a') as fd:
        for o in listvar_to_list(d, 'STANDBY_OBJECTS'):
            escaped = o.replace('/', '-')
            fd.write('Requires=mapper-wait@%s.service\n' % escaped)
            fd.write('After=mapper-wait@%s.service\n' % escaped)
}

do_install[postfuncs] += "add_standby_objects"
