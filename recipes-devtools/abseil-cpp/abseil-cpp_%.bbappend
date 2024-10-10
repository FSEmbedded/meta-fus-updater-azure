
FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

PV = "20240116.2+git${SRCPV}"
SRCREV = "d7aaad83b488fd62bd51c81ecf16cd938532cc0a"
BRANCH = "lts_2024_01_16"
SRC_URI = "git://github.com/abseil/abseil-cpp;branch=${BRANCH};protocol=https \
           file://0001-absl-always-use-asm-sgidefs.h.patch             \
           file://0002-Remove-maes-option-from-cross-compilation.patch \
           file://abseil-ppc-fixes.patch \
           file://0003-Remove-neon-option-from-cross-compilation.patch \
          "
