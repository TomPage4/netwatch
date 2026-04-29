package com.example.mob_dev_portfolio.data.constants

object EventType {

    const val FIRST_SEEN = "First seen"
    const val FULLY_RESOLVED = "Scan fully resolved"
    const val PARTIAL_RESOLVE = "Scan partial resolved"
    const val UNRESOLVED = "Scan unresolved"
    const val RESOLVE_FAILED = "Resolve failed"
    const val IP_UPDATED = "IP address updated"
    const val HOSTNAME_UPDATED = "Hostname updated"
    const val PORT_UPDATED = "Port number updated"
    const val TRUSTED_ON = "Service marked as trusted"
    const val TRUSTED_OFF = "Service removed as trusted"
    const val NEW_SERVICE = "New service discovered"
    const val PORT_SCANNED = "Port successfully scanned"
}