package com.example.mob_dev_portfolio.features.risk

import android.util.Log
import com.example.mob_dev_portfolio.data.constants.ServiceType
import com.example.mob_dev_portfolio.data.dto.RiskFindingDTO
import com.example.mob_dev_portfolio.data.entity.ResolveStatus
import com.example.mob_dev_portfolio.data.entity.RiskRule
import com.example.mob_dev_portfolio.data.entity.RiskRating
import com.example.mob_dev_portfolio.data.entity.ScanServiceHistoryEntity
import kotlin.math.roundToInt

class RiskAssessor {

    fun serviceAssess(
        serviceType: String? = null,
        ipAddress: String?,
        port: Int?,
        resolveStatus: ResolveStatus,
        serviceName: String? = null,
        isNewOnKnownNetwork: Boolean = false,
        riskRule: RiskRule
    ): Pair<RiskRating, List<RiskFindingDTO>> {

        val findings = mutableListOf<RiskFindingDTO>()

        when (serviceType) {
            ServiceType.HTTP -> findings.add(RiskFindingDTO(points = 3, RiskRating.MED, title = "Unencrypted service", detail = "HTTP sends data in plaintext, so anyone on the network could potentially read it"))
            ServiceType.HTTPS -> findings.add(RiskFindingDTO(points = 1, RiskRating.LOW, title = "Encrypted service", detail = "HTTPS encrypts data in transit, helping protect it from interception"))
            ServiceType.DNS_SD -> findings.add(RiskFindingDTO(points = 2, RiskRating.LOW, title = "Service advertisement", detail = "This device is announcing its presence and available services on the network"))
            ServiceType.GOOGLE_CAST -> findings.add(RiskFindingDTO(points = 2, RiskRating.LOW, title = "Remote control service", detail = "Nearby devices may be able to control media playback on this device"))
            ServiceType.AIRPLAY -> findings.add(RiskFindingDTO(points = 2, RiskRating.LOW, title = "Remote stream target", detail = "Nearby devices may be able to stream content to this device"))
            ServiceType.SPOTIFY_CONNECT -> findings.add(RiskFindingDTO(points = 1, RiskRating.LOW, title = "Account-linked playback", detail = "Requires Spotify login, so exposure to others is limited"))
            ServiceType.RAOP -> findings.add(RiskFindingDTO(points = 3, RiskRating.MED, title = "Legacy protocol", detail = "RAOP is an older audio streaming protocol with known security weaknesses"))
            ServiceType.IPP -> findings.add(RiskFindingDTO(points = 1, RiskRating.LOW, title = "Standard printer service", detail = "IPP is a widely used and established printing protocol"))
            ServiceType.PRINTER -> findings.add(RiskFindingDTO(points = 1, RiskRating.LOW, title = "Printer advertised", detail = "A printer is announcing its availability on this network"))
            ServiceType.SMB -> findings.add(RiskFindingDTO(points = 5, RiskRating.HIGH, title = "File sharing exposed", detail = "SMB enables file sharing. On public networks, this is a major risk and often used for lateral attacks"))
            ServiceType.WORKSTATION -> findings.add(RiskFindingDTO(points = 2, RiskRating.LOW, title = "Workstation present", detail = "A computer is announcing its presence on the network"))
            ServiceType.SSH -> findings.add(RiskFindingDTO(points = 4, RiskRating.MED, title = "Remote access advertised", detail = "SSH access is openly advertised. While legitimate, it is a common target on shared networks"))
            ServiceType.RDP -> findings.add(RiskFindingDTO(points = 5, RiskRating.HIGH, title = "Remote desktop advertised", detail = "RDP access is being broadcast. Remote desktop should not be exposed on shared networks"))
            ServiceType.RFB -> findings.add(RiskFindingDTO(points = 5, RiskRating.HIGH, title = "VNC remote access advertised", detail = "RFB (VNC) provides full remote desktop control and is often poorly secured"))
            ServiceType.HAP -> findings.add(RiskFindingDTO(points = 2, RiskRating.LOW, title = "Smart home device", detail = "A HomeKit device is present. Smart home devices on unknown networks should be noted"))
            ServiceType.HUE -> findings.add(RiskFindingDTO(points = 2, RiskRating.LOW, title = "Smart lighting present", detail = "A Philips Hue bridge is on the network. Smart home systems on public networks are unusual"))
            ServiceType.MATTER -> findings.add(RiskFindingDTO(points = 2, RiskRating.LOW, title = "Smart home device", detail = "A Matter-compatible device is present. These are uncommon on public networks"))
            else -> findings.add(RiskFindingDTO(points = 3, RiskRating.MED, title = "Unrecognised service type", detail = "This service type is unknown. It should be treated with caution"))
        }

        when (port) {
            null -> findings.add(RiskFindingDTO(points = 2, RiskRating.LOW, title = "Port not resolved", detail = "The port used by this service could not be identified"))
            23 -> findings.add(RiskFindingDTO(points = 7, RiskRating.HIGH, title = "Telnet detected", detail = "Telnet is unencrypted, so any credentials sent can be read by others on the network"))
            21 -> findings.add(RiskFindingDTO(points = 7, RiskRating.HIGH, title = "FTP detected", detail = "FTP sends usernames and passwords in plaintext, making them easy to intercept"))
            3389 -> findings.add(RiskFindingDTO(points = 7, RiskRating.HIGH, title = "Remote desktop exposed", detail = "RDP is commonly targeted by brute-force attacks and should not be exposed on shared networks"))
            5900 -> findings.add(RiskFindingDTO(points = 6, RiskRating.HIGH, title = "VNC detected", detail = "VNC provides full remote access and is often left poorly secured"))
            22 -> findings.add(RiskFindingDTO(points = 4, RiskRating.MED, title = "SSH port open", detail = "SSH enables remote terminal access. Legitimate, but frequently targeted"))
            80 -> findings.add(RiskFindingDTO(points = 3, RiskRating.MED, title = "Plain HTTP port", detail = "Port 80 serves unencrypted web traffic that can be intercepted"))
            25 -> findings.add(RiskFindingDTO(points = 4, RiskRating.MED, title = "SMTP port open", detail = "Mail transfer port. Uncommon on local networks and sometimes abused"))
            53 -> findings.add(RiskFindingDTO(points = 3, RiskRating.MED, title = "DNS port exposed", detail = "Open DNS services can be misused for traffic redirection or extract data"))
            8080 -> findings.add(RiskFindingDTO(points = 2, RiskRating.LOW, title = "Alternate HTTP port", detail = "Commonly used for web interfaces or development servers"))
            443 -> findings.add(RiskFindingDTO(points = 1, RiskRating.LOW, title = "HTTPS port", detail = "Standard port for encrypted web traffic and generally expected"))
            631 -> findings.add(RiskFindingDTO(points = 1, RiskRating.LOW, title = "Standard print port", detail = "Default IPP port, typically used by printers"))
            else -> findings.add(RiskFindingDTO(points = 2, RiskRating.LOW, title = "Non-standard port", detail = "Port ${port} is not commonly used and may require attention"))
        }

        when {
            ipAddress == null -> findings.add(RiskFindingDTO(points = 2, RiskRating.LOW, title = "IP not resolved", detail = "The IP address for this service could not be determined"))
            ipAddress.startsWith("127.") -> findings.add(RiskFindingDTO(points = 0, RiskRating.LOW, title = "Loopback address", detail = "This service is only accessible from the local device itself"))
            ipAddress.startsWith("192.168.") || ipAddress.startsWith("10.") -> findings.add(
                RiskFindingDTO(points = 1, RiskRating.LOW, title = "Private network address", detail = "The service is operating within a local private network range")
            )
            ipAddress.startsWith("169.254.") -> findings.add(RiskFindingDTO(points = 2, RiskRating.LOW, title = "Link-local address", detail = "Self-assigned address. The device may have failed to obtain a proper IP"))
            else -> findings.add(RiskFindingDTO(5, RiskRating.HIGH, title = "Public IP address", detail = "This service is accessible from the public internet, significantly increasing exposure"))
        }

        when (resolveStatus) {
            ResolveStatus.RESOLVED -> {}
            ResolveStatus.PARTIAL -> findings.add(RiskFindingDTO(1, RiskRating.LOW, title = "Partially resolved", detail = "Some service details could not be retrieved"))
            ResolveStatus.UNRESOLVED -> findings.add(RiskFindingDTO(2, RiskRating.MED, title = "Service unresolved", detail = "This service advertised itself but did not respond to resolution. It may be an error or deliberately evasive"))
        }

        if (serviceType == ServiceType.HTTP && port == 80) {
            findings.add(RiskFindingDTO(points = 2, RiskRating.MED, title = "Confirmed unencrypted traffic", detail = "Both the service type and port confirm this connection is unencrypted"))
        }

        if (serviceType == ServiceType.RDP && port == 3389) {
            findings.add(RiskFindingDTO(points = 3, RiskRating.HIGH, title = "Confirmed remote desktop exposure", detail = "Both the service type and port confirm RDP is openly accessible on this network"))
        }

        if (serviceType == ServiceType.RFB && port == 5900) {
            findings.add(RiskFindingDTO(points = 3, RiskRating.HIGH, title = "Confirmed VNC exposure", detail = "Both the service type and port confirm VNC remote access is openly accessible"))
        }

        if (serviceType == ServiceType.SMB && (ipAddress?.startsWith("192.168.") == true || ipAddress?.startsWith("10.") == true)) {
            findings.add(RiskFindingDTO(points = 2, RiskRating.MED, title = "File sharing on local network", detail = "SMB file sharing is active within the local network. Ensure access is restricted to trusted users only"))
        }

        if (serviceName != null) {
            if (serviceName.matches(Regex(".*\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}.*"))) {
                findings.add(RiskFindingDTO(points = 3, RiskRating.MED, title = "IP in service name", detail = "This service name contains an IP address, which is unusual and worth noting"))
            }
            if (serviceName.length > 40) {
                findings.add(RiskFindingDTO(points = 2, RiskRating.LOW, title = "Unusually long service name", detail = "Legitimate devices rarely use very long service names"))
            }
        }

        if (isNewOnKnownNetwork) {
            findings.add(RiskFindingDTO(points = 3, RiskRating.MED, title = "New on known network", detail = "This service is new on a network you have visited before"))
        }

        val rawScore = findings.sumOf { it.points }

        val finalScore = when (riskRule) {
            RiskRule.PERMISSIVE -> (rawScore * 0.7).roundToInt()
            RiskRule.STANDARD -> rawScore
            RiskRule.CONSERVATIVE -> (rawScore * 1.3).roundToInt()
        }

        val highestFinding = findings.maxOf { it.severity }

        val scoreRating = when {
            finalScore <= 6 -> RiskRating.LOW
            finalScore <= 11 -> RiskRating.MED
            else -> RiskRating.HIGH
        }

        val adjustHighestFinding = when (riskRule) {
            RiskRule.PERMISSIVE -> when (highestFinding) {
                RiskRating.LOW -> RiskRating.LOW
                RiskRating.MED -> RiskRating.LOW
                RiskRating.HIGH -> RiskRating.MED
                RiskRating.UNRATED -> RiskRating.UNRATED
            }
            RiskRule.STANDARD -> highestFinding
            RiskRule.CONSERVATIVE -> when (highestFinding) {
                RiskRating.LOW -> RiskRating.MED
                RiskRating.MED -> RiskRating.HIGH
                RiskRating.HIGH -> RiskRating.HIGH
                RiskRating.UNRATED -> RiskRating.UNRATED
            }
        }

        val rating = if (adjustHighestFinding >= scoreRating) {
            adjustHighestFinding
        } else {
            scoreRating
        }

        return Pair(rating, findings)
    }

    fun deviceAssess(
        ipAddress: String,
        services: List<ScanServiceHistoryEntity>? = null,
        isNewOnKnownNetwork: Boolean = false,
        riskRule: RiskRule
    ): Pair<RiskRating, List<RiskFindingDTO>> {

        val findings = mutableListOf<RiskFindingDTO>()
        val openPorts = services?.filter { it.port != null && it.portResponse != null } ?: emptyList()

        when {
            ipAddress.startsWith("127.") -> findings.add(RiskFindingDTO(0, RiskRating.LOW, title = "Loopback address", detail = "This device is only accessible from the local device itself"))
            ipAddress.startsWith("192.168.") || ipAddress.startsWith("10.") -> findings.add(RiskFindingDTO(1, RiskRating.LOW, title = "Private network address", detail = "This device is operating within a local private network range"))
            ipAddress.startsWith("169.254.") -> findings.add(RiskFindingDTO(2, RiskRating.LOW, title = "Link-local address", detail = "This device is using a self-assigned address and may have failed to obtain a proper IP configuration"))
            else -> findings.add(RiskFindingDTO(5, RiskRating.HIGH, title = "Public IP address", detail = "This device is accessible from the public internet, which significantly increases exposure"))
        }

        if (openPorts.isEmpty() && services.isNullOrEmpty()) {
            findings.add(RiskFindingDTO(points = 1, RiskRating.LOW, title = "No services or ports detected", detail = "This device is present on the network but is not advertising or exposing any detectable services"))
            return buildResult(findings, riskRule)
        }

        if (openPorts.isEmpty() && !services.isNullOrEmpty()) {
            findings.add(RiskFindingDTO(1, RiskRating.LOW, title = "No open ports detected", detail = "Services were identified on this device, but none responded to port probing"))
        }

        if (openPorts.isNotEmpty()) {
            for (service in openPorts) {

                val response = service.portResponse

                if (response != null && response != "OPEN (non-HTTP)") {

                    if (response.contains("Fat-Free Framework", ignoreCase = true) || response.contains("webpages/login", ignoreCase = true)) {
                        findings.add(RiskFindingDTO(points = 1, RiskRating.LOW, title = "Device identified: Router or gateway", detail = "The HTTP response suggests a router or gateway administration interface. Ensure default credentials have been changed"
                        ))
                    }

                    if (response.contains("GoAhead", ignoreCase = true)) {
                        findings.add(RiskFindingDTO(points = 4, RiskRating.MED, title = "Device identified: Embedded or IoT device", detail = "The GoAhead web server is commonly used in cameras, NAS devices, and network appliances. These devices may run outdated firmware and should be kept up to date"))
                    }

                    if (response.contains("lighttpd", ignoreCase = true)) {
                        findings.add(RiskFindingDTO(points = 3, RiskRating.MED, title = "Device identified: Embedded web service", detail = "lighttpd is a lightweight web server often used in embedded or IoT devices. Verify that this device is expected and properly maintained"))
                    }

                    if (response.contains("login.html", ignoreCase = true) || response.contains("/login", ignoreCase = true)) {
                        findings.add(RiskFindingDTO(points = 2, RiskRating.LOW, title = "Login interface exposed", detail = "A login interface is exposed on this device. Ensure strong credentials are in use"))
                    }
                }
            }

            if (openPorts.size >= 4) {
                findings.add(RiskFindingDTO(points = 3, RiskRating.MED, title = "Multiple open ports", detail = "This device has ${openPorts.size} open ports, which is more exposure than usual"))
            }
        }

        if (!services.isNullOrEmpty()) {
            val highRiskServices = services.filter { it.riskRating == RiskRating.HIGH }
            val medRiskServices  = services.filter { it.riskRating == RiskRating.MED }

            if (highRiskServices.isNotEmpty()) {
                findings.add(RiskFindingDTO(points = 5, RiskRating.HIGH, title = "${highRiskServices.size} high-risk service(s) detected", detail = "One or more services on this device are classified as high risk"))
            }
            if (medRiskServices.isNotEmpty()) {
                findings.add(RiskFindingDTO(points = 2, RiskRating.MED, title = "${medRiskServices.size} medium-risk service(s) detected", detail = "One or more services on this device are classified as medium risk"))
            }

            if (services.size >= 5) {
                findings.add(RiskFindingDTO(points = 3, RiskRating.MED, title = "High number of services", detail = "This device is advertising ${services.size} services, which is more than usual"))
            }
        }

        if (isNewOnKnownNetwork) {
            findings.add(RiskFindingDTO(points = 3, RiskRating.MED, title = "New device on known network", detail = "This device has not been previously observed on a network you have visited before"))
        }

        return buildResult(findings, riskRule)
    }

    private fun buildResult(
        findings: MutableList<RiskFindingDTO>,
        riskRule: RiskRule
    ): Pair<RiskRating, List<RiskFindingDTO>> {
        val rawScore = findings.sumOf { it.points }

        val finalScore = when (riskRule) {
            RiskRule.PERMISSIVE -> (rawScore * 0.7).roundToInt()
            RiskRule.STANDARD -> rawScore
            RiskRule.CONSERVATIVE -> (rawScore * 1.3).roundToInt()
        }

        val scoreRating = when {
            finalScore <= 9 -> RiskRating.LOW
            finalScore <= 16 -> RiskRating.MED
            else -> RiskRating.HIGH
        }

        val highestFinding = findings.maxOf { it.severity }

        val adjustedHighestFinding = when (riskRule) {
            RiskRule.PERMISSIVE -> when (highestFinding) {
                RiskRating.LOW -> RiskRating.LOW
                RiskRating.MED  -> RiskRating.LOW
                RiskRating.HIGH -> RiskRating.MED
                RiskRating.UNRATED -> RiskRating.UNRATED
            }
            RiskRule.STANDARD -> highestFinding
            RiskRule.CONSERVATIVE -> when (highestFinding) {
                RiskRating.LOW -> RiskRating.LOW
                RiskRating.MED -> RiskRating.HIGH
                RiskRating.HIGH -> RiskRating.HIGH
                RiskRating.UNRATED -> RiskRating.UNRATED
            }
        }

        val rating = if (adjustedHighestFinding >= scoreRating) {
            adjustedHighestFinding
        } else {
            scoreRating
        }

        return Pair(rating, findings)
    }
}