DESCRIPTION="mqtt-rpi0"

LICENSE="Apache-2.0"
LIC_FILES_CHKSUM = "file://${WORKDIR}/git/LICENSE;md5=1dece7821bf3fd70fe1309eaa37d52a2"

# Patches for quilt goes to files directory
FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI = "git://git@github.com/ARMmbed/mqtt-rpi0.git;protocol=ssh; \
           file://mqttpt.init \
           file://mqttpt-example \
           "

SRCREV = "master"

# Installed packages
PACKAGES = "${PN} ${PN}-dbg"
FILES_${PN} += "/wigwag \
                /wigwag/mbed \
                /wigwag/mbed/mqttpt-example"

FILES_${PN}-dbg += "/wigwag/mbed/.debug \
                    /usr/src/debug/mqtt-rpi0"

S = "${WORKDIR}/git"

DEPENDS = " libcap mosquitto glib-2.0"
RDEPENDS_${PN} = " procps start-stop-daemon bash bluez5 virtual/mbed-edge-core"

EXTRA_OECMAKE += " \
    -DTARGET_TOOLCHAIN=yocto \
    -DCMAKE_BUILD_TYPE=Release \
    ${MBED_EDGE_CUSTOM_CMAKE_ARGUMENTS} "
inherit cmake

inherit update-rc.d
INITSCRIPT_NAME = "mqttpt"
INITSCRIPT_PARAMS = "defaults 86 15"

do_configure_prepend() {
    cd ${S}
}

do_install() {
    install -d "${D}/wigwag/mbed"
    install "${WORKDIR}/build/bin/mqttpt-example" "${D}/wigwag/mbed"
    install "${WORKDIR}/git/mqttpt-example/mqttgw_sim/mqtt_ep.sh" "${D}/wigwag/mbed"
    install "${WORKDIR}/git/mqttpt-example/mqttgw_sim/mqtt_gw.sh" "${D}/wigwag/mbed"

    install -d "${D}${sysconfdir}/logrotate.d"
    install -m 644 "${WORKDIR}/mqttpt-example" "${D}${sysconfdir}/logrotate.d"

    install -d "${D}${sysconfdir}/init.d/"
    install -m 755 "${WORKDIR}/mqttpt.init" "${D}${sysconfdir}/init.d/${INITSCRIPT_NAME}"
}
