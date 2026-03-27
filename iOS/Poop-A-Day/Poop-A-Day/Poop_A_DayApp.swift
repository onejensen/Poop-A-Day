import SwiftUI
import GoogleMobileAds
import AppTrackingTransparency

@main
struct Poop_A_DayApp: App {
    var body: some Scene {
        WindowGroup {
            ContentView()
                .onReceive(NotificationCenter.default.publisher(for: UIApplication.didBecomeActiveNotification)) { _ in
                    requestTrackingPermission()
                }
        }
    }

    private func requestTrackingPermission() {
        ATTrackingManager.requestTrackingAuthorization { status in
            MobileAds.shared.start(completionHandler: nil)
        }
    }
}
