import Foundation

enum AdConfig {
    // Set to false to use production IDs
    static let useTestAds = false

    static var bannerAdUnitID: String {
        useTestAds
            ? "ca-app-pub-3940256099942544/2934735716"
            : "ca-app-pub-5686344303496334/9649069624"
    }

    static var interstitialAdUnitID: String {
        useTestAds
            ? "ca-app-pub-3940256099942544/4411468910"
            : "ca-app-pub-5686344303496334/1076311584"
    }
}
