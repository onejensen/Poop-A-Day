import UIKit
import GoogleMobileAds

class InterstitialAdManager {
    private var interstitialAd: InterstitialAd?
    private let adUnitID: String

    init(adUnitID: String) {
        self.adUnitID = adUnitID
        loadAd()
    }

    func loadAd() {
        InterstitialAd.load(with: adUnitID, request: Request()) { [weak self] ad, error in
            if let error = error {
                print("Failed to load interstitial: \(error.localizedDescription)")
                return
            }
            self?.interstitialAd = ad
        }
    }

    func showAd() {
        guard let ad = interstitialAd,
              let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
              let rootVC = windowScene.windows.first?.rootViewController else {
            loadAd()
            return
        }
        ad.present(from: rootVC)
        loadAd()
    }
}
