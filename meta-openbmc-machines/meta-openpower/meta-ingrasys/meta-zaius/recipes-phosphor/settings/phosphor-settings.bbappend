inherit pythonnative

# Not using do_configure_append_zaius because do_configure is a shell function.
# Instead, add a new task to handle overrides.
python do_override_defaults () {
    # Python functions in BitBake aren't run by python-native, so add native
    # packages to Python path.
    packages = os.path.join(d.getVar('STAGING_DIR_NATIVE', True),
                            '.' + d.getVar('PYTHON_SITEPACKAGES_DIR', True))
    sys.path.append(os.path.normpath(packages))

    import yaml
    settings_file = os.path.join(d.getVar('S', True), 'settings.yaml')
    with open(settings_file) as s:
        data = yaml.safe_load(s)

    # Override the settings data
    data['org.openbmc.settings.Host']['settings']['powerpolicy']['default'] = 'ALWAYS_POWER_ON'

    with open(settings_file, 'w') as s:
        data = yaml.dump(data, s)
}

python () {
    # There is no equivalent to overrides-style ("_zaius") syntax for addtask
    if d.getVar('MACHINE', True) == 'zaius':
        bb.build.addtask('do_override_defaults', 'do_compile', 'do_configure',
                         d)
}
