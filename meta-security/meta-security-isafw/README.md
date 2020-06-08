**meta-security-isafw** is an OE layer that allows enabling the Image
Security Analysis Framework (isafw) for your image builds. 

The primary purpose of isafw is to provide an extensible 
framework for analysing different security aspects of images 
during the build process.

The isafw project itself can be found at 
    https://github.com/01org/isafw

The framework supports a number of callbacks (such as 
process_package(), process_filesystem(), and etc.) that are invoked 
by the bitbake during different stages of package and image build. 
These callbacks are then forwarded for processing to the avaliable 
ISA FW plugins that have registered for these callbacks. 
Plugins can do their own processing on each stage of the build 
process and produce security reports. 

Dependencies
------------

The **meta-security-isafw** layer depends on the Open Embeeded
core layer:

    git://git.openembedded.org/openembedded-core


Usage
-----

In order to enable the isafw during the image build, please add 
the following line to your build/conf/local.conf file:

```python
INHERIT += "isafw"
```

Next you need to update your build/conf/bblayers.conf file with the
location of meta-security-isafw layer on your filesystem along with
any other layers needed. e.g.:

```python
BBLAYERS ?= " \
  /OE/oe-core/meta \
  /OE/meta-security/meta-security-isafw \
  "
```
 
Also, some isafw plugins require network connection, so in case of a
proxy setup please make sure to export http_proxy variable into your 
environment.

In order to produce image reports, you can execute image build 
normally. For example:

```shell
bitbake core-image-minimal
```

If you are only interested to produce a report based on packages 
and without building an image, please use:

```shell
bitbake -c analyse_sources_all core-image-minimal
```


Logs
----

All isafw plugins by default create their logs under the 
${LOG_DIR}/isafw-report/ directory, where ${LOG_DIR} is a bitbake 
default location for log files. If you wish to change this location, 
please define ISAFW_REPORTDIR variable in your local.conf file. 

Patches
-------
end pull requests, patches, comments or questions to yocto@lists.yoctoproject.org

When sending single patches, please using something like:
'git send-email -1 --to yocto@lists.yoctoproject.org --subject-prefix=meta-security-isafw][PATCH'

These values can be set as defaults for this repository:

$ git config sendemail.to yocto@lists.yoctoproject.org
$ git config format.subjectPrefix meta-security-isafw][PATCH

Now you can just do 'git send-email origin/master' to send all local patches.

For pull requests, please use create-pull-request and send-pull-request.

Maintainers:    Armin Kuster <akuster808@gmail.com>
