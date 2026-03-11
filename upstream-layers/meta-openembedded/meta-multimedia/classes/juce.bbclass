inherit pkgconfig

# List of JUCE modules that requires external dependencies
JUCE_MODULES ??= " \
  juce_core \
  ${@bb.utils.contains('DISTRO_FEATURES', 'alsa', 'juce_audio_devices', '', d)} \
  ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'juce_graphics juce_gui_basics', '', d)} \
  ${@bb.utils.contains('DISTRO_FEATURES', 'opengl', 'juce_opengl', '', d)} \
"
JUCE_X11_DEPS = "libx11 libxext libxinerama libxrandr libxcursor"

PACKAGECONFIG:prepend= "${JUCE_MODULES} "
PACKAGECONFIG[juce_core] = ",,curl,"
PACKAGECONFIG[juce_audio_devices] = ",,alsa-lib,"
PACKAGECONFIG[juce_graphics] = ",,${JUCE_X11_DEPS} freetype,"
PACKAGECONFIG[juce_gui_basics] = ",,${JUCE_X11_DEPS},"
PACKAGECONFIG[juce_opengl] = ",,virtual/libgl,"

DEPENDS:prepend = "projucer-native "

export OE_JUCE_PROJUCER = "${STAGING_BINDIR_NATIVE}/Projucer"

juce_do_configure() {
  if [ -z "${JUCE_JUCERS}" ]; then
    JUCERS=`find . -type f -iname "*.jucer"` && IFS=$'\n'
  else
    JUCERS="${JUCE_JUCERS}"
  fi

  if [ -z "$JUCERS" ]; then
    die "JUCE_JUCERS not set and no profiles found in $PWD"
  fi

  # XXX: Hack for Projucer, since it requires a X Display even when running in
  # console mode. This will be fixed in future. Most cases DISPLAY=:0 will just work,
  # the only case why we have JUCE_DISPLAY variable, is in case of a build system,
  # such as jenkins, that can have multiple virtual X server running for each build.
  test -z "${JUCE_DISPLAY}" && export DISPLAY=:0 || export DISPLAY=${JUCE_DISPLAY}

  for i in $JUCERS; do
    ${OE_JUCE_PROJUCER} --resave $i
  done
}

EXPORT_FUNCTIONS do_configure

addtask configure after do_unpack do_patch before do_compile
