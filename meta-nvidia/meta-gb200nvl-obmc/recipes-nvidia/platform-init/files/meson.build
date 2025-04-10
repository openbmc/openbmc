project(
    'platform-init',
    'cpp',
    version: '0.1',
    default_options: [
        'warning_level=3',
        'cpp_std=c++23',
    ],
)

gpiodcxx = dependency('libgpiodcxx', default_options: ['bindings=cxx'])
systemd = dependency('systemd')
libsystemd = dependency('libsystemd')
libexec = get_option('prefix') + '/' + get_option('libexecdir')

exe = executable(
    'platform-init',
    'platform_init.cpp',
    dependencies: [
        gpiodcxx,
        libsystemd,
    ],
    install: true,
    install_dir: libexec,
)
systemd_system_unit_dir = systemd.get_variable('systemdsystemunitdir')
install_data('platform_init.service', install_dir: systemd_system_unit_dir)
