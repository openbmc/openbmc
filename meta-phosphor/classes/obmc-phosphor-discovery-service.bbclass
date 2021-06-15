inherit obmc-phosphor-utils
DISCOVERY_SVC_PACKAGES ?= "${PN}"

python() {
    avahi_enabled = bb.utils.contains(
            'DISTRO_FEATURES', 'avahi', True, False, d)
    slp_enabled = bb.utils.contains(
            'DISTRO_FEATURES', 'slp', True, False, d)

    if not avahi_enabled and not slp_enabled:
        return

    syscnfdir = d.getVar('sysconfdir', True)
    dest_dir = d.getVar('D', True)

    set_append(d, 'AVAHI_SERVICES_DIR', os.path.join(
        dest_dir+syscnfdir,
        'avahi',
        'services'))
    set_append(d, 'SLP_SERVICES_DIR', os.path.join(
        dest_dir+syscnfdir,
        'slp',
        'services'))


    for pkg in listvar_to_list(d, 'DISCOVERY_SVC_PACKAGES'):
        for service in listvar_to_list(d, 'REGISTERED_SERVICES_%s' % pkg):
            if avahi_enabled:
                set_append(d, 'RRECOMMENDS_%s' % pkg, 'avahi-daemon')
                svc_name, svc_type, svc_port, svc_txt_data = service.split(':')
                set_append(d, 'FILES_%s' % pkg, os.path.join(
                    syscnfdir,
                    'avahi',
                    'services',
                    '%s.service' % svc_name))

            if slp_enabled:
                set_append(d, 'RRECOMMENDS_%s' % pkg, 'slpd-lite')
                svc_name, svc_type, svc_port, svc_txt_data = service.split(':')
                set_append(d, 'FILES_%s' % pkg, os.path.join(
                    syscnfdir,
                    'slp',
                    'services',
                    '%s.service' % svc_name))

}

python discovery_services_postinstall() {
    avahi_enabled = bb.utils.contains(
            'DISTRO_FEATURES', 'avahi', True, False, d)
    slp_enabled = bb.utils.contains(
            'DISTRO_FEATURES', 'slp', True, False, d)

    if not avahi_enabled and not slp_enabled:
        return

    avahi_service_dir = d.getVar('AVAHI_SERVICES_DIR', True).strip()
    slp_service_dir = d.getVar('SLP_SERVICES_DIR', True).strip()

    if not os.path.exists(avahi_service_dir):
        os.makedirs(avahi_service_dir)

    if not os.path.exists(slp_service_dir):
        os.makedirs(slp_service_dir)

    def register_service_avahi(d, service_name, service_type, service_port, service_txt_data):
        service_txt_data = service_txt_data.split('|')
        service_file = os.path.join(
            avahi_service_dir,
            '%s.service' % service_name)
        with open(service_file, 'w') as fd:
            fd.write('<?xml version="1.0" ?>\n')
            fd.write('<!DOCTYPE service-group SYSTEM "avahi-service.dtd">\n')
            fd.write('<service-group>\n')
            fd.write('        <name replace-wildcards="yes">%s on %%h</name>\n'
                % service_name)
            fd.write('        <service>\n')
            fd.write('                <type>%s</type>\n' % service_type)
            fd.write('                <port>%s</port>\n' % service_port)
            for txt_record in service_txt_data:
                if txt_record:
                    key, value = txt_record.split('=')
                    if key.strip() and value.strip():
                        fd.write('                <txt-record>%s</txt-record>\n' % txt_record)
                    else:
                        bb.fatal('Invalid Additional data : \'%s\'. Ignoring!' % txt_record)
            fd.write('        </service>\n')
            fd.write('</service-group>\n')

    def register_service_slp(d, service_name, service_type, service_port):
        service_file = os.path.join(
            slp_service_dir,
            '%s.service' % service_name)
        with open(service_file, 'w') as fd:
            fd.write('%s %s %s' % (service_name, service_type, service_port))

    def register_services(d,pkg):
        for service in listvar_to_list(d, 'REGISTERED_SERVICES_%s' % pkg):
            svc_info = service.split(":")
            try:
                svc_name, svc_type, svc_port, svc_txt_data = svc_info
            except:
                continue
            if avahi_enabled:
                avahi_svc_type = "_" + svc_name + "._" + svc_type
                register_service_avahi(d, svc_name, avahi_svc_type, svc_port, svc_txt_data)
            if slp_enabled:
                register_service_slp(d, svc_name, svc_type, svc_port)

    for pkg in listvar_to_list(d, 'DISCOVERY_SVC_PACKAGES'):
        register_services(d, pkg)

}
do_install[postfuncs] += "discovery_services_postinstall"
