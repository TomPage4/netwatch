package com.example.mob_dev_portfolio.data.constants

object ServiceType {

    const val HTTP = "_http._tcp.local."
    const val HTTPS = "_https._tcp.local."
    const val DNS_SD = "_dns-sd._udp.local."
    const val GOOGLE_CAST = "_googlecast._tcp.local."
    const val AIRPLAY = "_airplay._tcp.local."
    const val SPOTIFY_CONNECT = "_spotify-connect._tcp.local."
    const val RAOP = "_raop._tcp.local."
    const val IPP = "_ipp._tcp.local."
    const val PRINTER = "_printer._tcp.local."
    const val SMB = "_smb._tcp.local."
    const val WORKSTATION = "_workstation._tcp.local."
    const val SSH = "_ssh._tcp.local."
    const val RDP = "_rdp._tcp.local."
    const val RFB = "_rfb._tcp.local."
    const val HAP = "_hap._tcp.local."
    const val HUE = "_hue._tcp.local."
    const val MATTER = "_matter._tcp.local."
    const val TELNET = "_telnet._tcp.local."
    const val FTP = "_ftp._tcp.local."
    const val RTSP = "_rtsp._tcp.local."

    val allServiceTypes = listOf(
        HTTP,
        HTTPS,
        DNS_SD,
        GOOGLE_CAST,
        AIRPLAY,
        SPOTIFY_CONNECT,
        RAOP,
        IPP,
        PRINTER,
        SMB,
        WORKSTATION,
        SSH,
        RDP,
        RFB,
        HAP,
        HUE,
        MATTER,
        TELNET,
        FTP,
        RTSP
    )
}