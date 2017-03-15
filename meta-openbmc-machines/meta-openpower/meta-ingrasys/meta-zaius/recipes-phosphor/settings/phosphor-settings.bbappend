python do_override_defaults () {
    import yaml
    settings_file = d.getVar('S', True) + '/settings.yaml'
    with open(settings_file) as s:
        data = yaml.safe_load(s)

    data['org.openbmc.settings.Host']['settings']['powerpolicy']['default'] = 'ALWAYS_POWER_ON'

    with open(settings_file, 'w') as s:
        data = yaml.dump(data, s)
}

python () {
    # There is no equivalent to overrides-style ("_zaius") syntax for addtask
    if d.getVar('MACHINE', True) == 'zaius':
        bb.build.addtask('do_override_defaults', 'do_compile', 'do_patch', d)
}
