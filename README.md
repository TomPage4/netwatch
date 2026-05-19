# NETWATCH

NETWATCH is an Android network scanning application designed to give non technical users meaningful visibility into devices and services connected to their local Wi-Fi network. The project was built around the idea that most users have very little awareness of what exists on a shared network, especially in environments such as public Wi-Fi, shared accommodation, universities, or small offices.

The application combines passive service discovery with active host discovery and port scanning to build a broader picture of the local network, including devices that do not advertise themselves. Rather than presenting raw networking data alone, NETWATCH translates findings into plain English risk assessments so users can better understand whether a network environment appears trustworthy or potentially unsafe.

## Overview

Most network scanner tools are designed for technical users and assume networking knowledge. NETWATCH instead focuses on accessibility and situational awareness.

The app continuously builds a view of the current network by:

- Discovering advertised services using mDNS
- Actively identifying hosts on the local subnet
- Performing lightweight port scans on discovered devices
- Tracking changes between scans over time
- Generating human readable risk explanations

The goal is not surveillance or penetration testing, but helping users make informed decisions about whether they should trust a network environment.

## Key features

### Combined network discovery

NETWATCH uses two discovery methods together:

- mDNS service discovery
- Active host discovery

This allows the app to identify both devices that openly advertise services and devices that remain silent on the network.

Discovered services are grouped by device using IP address correlation, helping users understand which services belong to which host.

### Port scanning

For each discovered host, the app performs lightweight scans on a predefined set of commonly used ports.

This helps detect potentially exposed services even when no mDNS advertisements are present.

### Risk assessment system

Every discovered device and service is assigned a risk rating:

- Low
- Medium
- High

Risk calculations consider factors such as:

- Service type
- Port number
- Resolve completeness
- Device naming
- IP address type
- Newly observed devices or services
- Previously unseen behaviour on known networks

Rather than only presenting a score, the app explains findings in plain English so users understand why something may be considered suspicious.

Example findings include:

- "This service uses a commonly exposed remote access port."
- "This device was not present on previous visits to this network."
- "The hostname could not be resolved."

Users can also choose between stricter or more permissive risk rule sets depending on how sensitive they want detections to be.

### Persistent network history

NETWATCH stores scan history locally on the device, organised by network.

This allows users to:

- Review previous scans
- Compare changes over time
- Detect new devices or services
- Identify unusual network behaviour

Each network includes a recent changes view showing:

- New devices
- Changed devices
- Newly exposed services

Changes can be filtered by:

- Last scan
- Last 24 hours
- Last 7 days

### Device and service event tracking

The app maintains historical logs for devices and services, recording events such as:

- First seen
- Hostname changes
- IP address changes
- Port changes

Each event is timestamped to help users understand how the network evolves over time.

### Privacy focused design

All scan data remains entirely on device.

The app:

- Sends no collected data externally
- Stores only information required for app functionality
- Allows all stored data to be permanently deleted at any time
- Supports configurable automatic data retention periods

## Application structure

The application is organised around several primary screens accessible through a navigation drawer.

### Dashboard

The default launch screen providing:

- Scan controls
- Live discovery results
- Current network summary
- Risk overview

### Network history

Displays previously scanned networks and their associated scan history.

Selecting a network opens a detailed network view containing:

- Historical scans
- Device lists
- Recent network changes

### Device detail screen

Provides detailed information about an individual device, including:

- Observed services
- Risk findings
- Historical events
- Previous changes

### Service detail screen

Displays service specific information such as:

- Service type
- Hostname
- IP address
- Port
- Resolve status
- Risk explanations

### Settings

Allows configuration of:

- Discovery methods
- Enabled mDNS service types
- Risk strictness
- Data retention policies

## Technical highlights

### JmDNS

JmDNS is used for mDNS service discovery.

Android’s built in NSD API was intentionally avoided due to its limited handling of multiple concurrent service types and unreliable resolution behaviour on unstable networks. JmDNS provided significantly more control and resilience when handling incomplete or inconsistent responses.

### dnsjava

dnsjava is used for reverse DNS lookups on actively discovered hosts.

Without reverse lookups, devices that do not advertise services would appear only as IP addresses, reducing the usefulness of the scan results.

### Jetpack Compose

The UI is built entirely using Jetpack Compose and Material3.

Compose works particularly well for continuously updating scan data, allowing discovery results and risk findings to update reactively without complex manual UI handling.

### Room

Room is used for persistent local storage.

The database stores:

- Networks
- Scan sessions
- Devices
- Services
- Historical events

This enables long term tracking and network change detection.

### ViewModel and Lifecycle components

ViewModels isolate scanning and persistence logic from UI code and ensure scans survive configuration changes such as device rotation.

### Kotlin Coroutines

All scanning operations run asynchronously using Kotlin coroutines to prevent blocking the main thread and to keep the application responsive during active scans.

### Gson

Gson is used for serialising structured list data into JSON for storage within Room, simplifying persistence without requiring overly complex relational schemas.

## Design decisions

A major focus throughout development was balancing technical depth with accessibility.

Many network tools expose large amounts of low level information without context. NETWATCH instead attempts to explain findings in a way that remains useful to users without networking expertise.

Another key design decision was combining passive and active discovery techniques. Relying only on mDNS would miss many devices entirely, while relying only on active scanning would lose valuable service metadata. Using both approaches together provides a more complete network picture.

The app was also designed around ongoing awareness rather than one off scans. Persisting scan history and tracking meaningful changes over time makes unusual behaviour easier to identify.

## Non functional considerations

The app was designed with several usability and reliability goals:

- Scan results begin appearing within seconds
- The UI remains responsive during active scans
- Discovery results stream live as devices are found
- Null or incomplete responses are handled safely
- Risk indicators are not dependent on colour alone
- Plain English explanations require no networking expertise
- The app supports both portrait and landscape orientation

## Lessons learned

NETWATCH was my first large Android project involving concurrent networking operations, persistent local storage, and continuously updating UI state.

The project gave me practical experience with:

- Android networking APIs
- Concurrent background processing
- Reactive UI design
- Local persistence architecture
- Risk modelling
- Usability focused security tooling
- Designing technical systems for non technical users

It also reinforced the importance of presenting technical information in a way that remains understandable and actionable to a wider audience.