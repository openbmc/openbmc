# Processes bitbake-user-manual (<word>-<word>-<word> style).
# This style is for manual three-word folders, which currently is only the BitBake User Manual.
# We used to have the "yocto-project-qs" and "poky-ref-manual" folders but no longer do.
# s@"ulink" href="http://www.yoctoproject.org/docs/3.1.4/[a-z]*-[a-z]*-[a-z]*/[a-z]*-[a-z]*-[a-z]*.html#@"link" href="#@g
s@"ulink" href="http://www.yoctoproject.org/docs/3.1.4/bitbake-user-manual/bitbake-user-manual.html#@"link" href="#@g

# Processes all other manuals (<word>-<word> style).
# This style is for manual folders that use two word, which is the standard now (e.g. "ref-manual").
# Here is the one-liner:
# s@"ulink" href="http://www.yoctoproject.org/docs/3.1.4/[a-z]*-[a-z]*/[a-z]*-[a-z]*.html#@"link" href="#@g

s@"ulink" href="http://www.yoctoproject.org/docs/3.1.4/sdk-manual/sdk-manual.html#@"link" href="#@g
s@"ulink" href="http://www.yoctoproject.org/docs/3.1.4/bsp-guide/bsp-guide.html#@"link" href="#@g
s@"ulink" href="http://www.yoctoproject.org/docs/3.1.4/dev-manual/dev-manual.html#@"link" href="#@g
s@"ulink" href="http://www.yoctoproject.org/docs/3.1.4/overview-manual/overview-manual.html#@"link" href="#@g
s@"ulink" href="http://www.yoctoproject.org/docs/3.1.4/brief-yoctoprojectqs/brief-yoctoprojectqs.html#@"link" href="#@g
s@"ulink" href="http://www.yoctoproject.org/docs/3.1.4/kernel-dev/kernel-dev.html#@"link" href="#@g
s@"ulink" href="http://www.yoctoproject.org/docs/3.1.4/profile-manual/profile-manual.html#@"link" href="#@g
s@"ulink" href="http://www.yoctoproject.org/docs/3.1.4/ref-manual/ref-manual.html#@"link" href="#@g
s@"ulink" href="http://www.yoctoproject.org/docs/3.1.4/toaster-manual/toaster-manual.html#@"link" href="#@g

# Process cases where just an external manual is referenced without an id anchor
s@<a class="ulink" href="http://www.yoctoproject.org/docs/3.1.4/brief-yoctoprojectqs/brief-yoctoprojectqs.html" target="_top">Yocto Project Quick Build</a>@Yocto Project Quick Build@g
s@<a class="ulink" href="http://www.yoctoproject.org/docs/3.1.4/bitbake-user-manual/bitbake-user-manual.html" target="_top">BitBake User Manual</a>@BitBake User Manual@g
s@<a class="ulink" href="http://www.yoctoproject.org/docs/3.1.4/dev-manual/dev-manual.html" target="_top">Yocto Project Development Tasks Manual</a>@Yocto Project Development Tasks Manual@g
s@<a class="ulink" href="http://www.yoctoproject.org/docs/3.1.4/overview-manual/overview-manual.html" target="_top">Yocto Project Overview and Concepts Manual</a>@Yocto project Overview and Concepts Manual@g
s@<a class="ulink" href="http://www.yoctoproject.org/docs/3.1.4/sdk-manual/sdk-manual.html" target="_top">Yocto Project Application Development and the Extensible Software Development Kit (eSDK)</a>@Yocto Project Application Development and the Extensible Software Development Kit (eSDK)@g
s@<a class="ulink" href="http://www.yoctoproject.org/docs/3.1.4/bsp-guide/bsp-guide.html" target="_top">Yocto Project Board Support Package (BSP) Developer's Guide</a>@Yocto Project Board Support Package (BSP) Developer's Guide@g
s@<a class="ulink" href="http://www.yoctoproject.org/docs/3.1.4/profile-manual/profile-manual.html" target="_top">Yocto Project Profiling and Tracing Manual</a>@Yocto Project Profiling and Tracing Manual@g
s@<a class="ulink" href="http://www.yoctoproject.org/docs/3.1.4/kernel-dev/kernel-dev.html" target="_top">Yocto Project Linux Kernel Development Manual</a>@Yocto Project Linux Kernel Development Manual@g
s@<a class="ulink" href="http://www.yoctoproject.org/docs/3.1.4/ref-manual/ref-manual.html" target="_top">Yocto Project Reference Manual</a>@Yocto Project Reference Manual@g
s@<a class="ulink" href="http://www.yoctoproject.org/docs/3.1.4/toaster-manual/toaster-manual.html" target="_top">Toaster User Manual</a>@Toaster User Manual@g

# Process a single, rouge occurrence of a linked reference to the Mega-Manual.
s@<a class="ulink" href="http://www.yoctoproject.org/docs/3.1.4/mega-manual/mega-manual.html" target="_top">Yocto Project Mega-Manual</a>@Yocto Project Mega-Manual@g

