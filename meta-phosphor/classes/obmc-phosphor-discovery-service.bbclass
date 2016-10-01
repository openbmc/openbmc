inherit obmc-phosphor-utils
DISCOVERY_SVC_PACKAGES ?= "${PN}"

python() {
    avahi_enabled = bb.utils.contains(
            'DISTRO_FEATURES', 'avahi', 'true', 'false', d)
    slp_enabled = False # later

    if not avahi_enabled and slp_enabled:
        return

    syscnfdir = d.getVar('sysconfdir', True)
    dest_dir = d.getVar('D', True)
    set_append(d, 'AVAHI_SERVICES_DIR', os.path.join(
        dest_dir+syscnfdir,
        'avahi',
        'services'))

    for pkg in listvar_to_list(d, 'DISCOVERY_SVC_PACKAGES'):
        for service in listvar_to_list(d, 'REGISTERED_SERVICES_%s' % pkg):
            if avahi_enabled:
                set_append(d, 'RRECOMMENDS_%s' % pkg, 'avahi-daemon')
                svc_name, svc_type, svc_port = service.split(':')
                set_append(d, 'FILES_%s' % pkg, os.path.join(
                    syscnfdir,
                    'avahi',
                    'services',
                    '%s.service' % svc_name))
}

python discovery_services_postinstall() {
    avahi_enabled = bb.utils.contains(
            'DISTRO_FEATURES', 'avahi', 'true', 'false', d)
    slp_enabled = False # later

    if not avahi_enabled and slp_enabled:
        return

    service_dir = d.getVar('AVAHI_SERVICES_DIR', True)

    if not os.path.exists(service_dir):
        os.makedirs(service_dir)

    def register_service_avahi(d, service_name, service_type, service_port):
        service_file = os.path.join(
            service_dir,
            '%s.service' % service_name)
        with open(service_file, 'w') as fd:
            fd.write('<?xml version="1.0" ?>\n')
            fd.write('<!DOCTYPE service-group SYSTEM "avahi-service.dtd">\n')
            fd.write('<service-group>\n')
            fd.write('        <name>"%s"</name>\n' % service_name)
            fd.write('        <service>\n')
            fd.write('                <type>"%s"</type>\n' % service_type)
            fd.write('                <port>"%s"</port>\n' % service_port)
            fd.write('        </service>\n')
            fd.write('</service-group>\n')

    def register_services(d,pkg):
        for service in listvar_to_list(d, 'REGISTERED_SERVICES_%s' % pkg):
            svc_info = service.split(":")
            try:
                svc_name, svc_type, svc_port = svc_info
            except:
                continue
            if avahi_enabled:
                register_service_avahi(d, svc_name, svc_type, svc_port)

    for pkg in listvar_to_list(d, 'DISCOVERY_SVC_PACKAGES'):
        register_services(d, pkg)

}
do_install[postfuncs] += "discovery_services_postinstall"


