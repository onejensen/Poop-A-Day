import SwiftUI
import AVFoundation

// MARK: - Splash Droplet

struct SplashDroplet: Identifiable {
    let id = UUID()
    let angle: Double
    let distance: CGFloat
    let size: CGFloat
}

// MARK: - Tracker View

struct TrackerView: View {
    @ObservedObject var store: DataStore
    @Binding var isDarkMode: Bool
    @State private var interstitialAd = InterstitialAdManager(adUnitID: AdConfig.interstitialAdUnitID)

    // Poop animation states
    @State private var poopY: CGFloat = -500
    @State private var poopRotation: Double = 0
    @State private var poopScale: CGFloat = 1.0
    @State private var poopOpacity: Double = 0.0

    // Toilet animation states
    @State private var toiletShake: CGFloat = 0
    @State private var toiletScale: CGFloat = 1.0

    // Splash animation
    @State private var showSplash = false
    @State private var splashDroplets: [SplashDroplet] = []
    @State private var splashProgress: CGFloat = 0

    // Counter pop animation
    @State private var counterScale: CGFloat = 1.0

    // Prevent spam tapping during animation
    @State private var isAnimating = false
    @State private var audioPlayer: AVAudioPlayer?

    // Toilet sits slightly above center
    private let toiletY: CGFloat = 40

    var todayCount: Int {
        store.getStats().today
    }

    var body: some View {
        ZStack {
            // Background - bathroom tiles
            TileBackground()
                .ignoresSafeArea()

            // Top bar: counter
            VStack {
                HStack(spacing: 4) {
                    Text("💩")
                        .font(.system(size: 28))
                    Text("×\(todayCount)")
                        .font(.system(size: 32, weight: .bold, design: .rounded))
                        .foregroundColor(.brown)
                        .scaleEffect(counterScale)
                }
                .padding(.top, 40)

                Text("today")
                    .font(.system(size: 14, weight: .medium, design: .rounded))
                    .foregroundColor(.gray)

                // Streak
                if store.getStreak() > 1 {
                    HStack(spacing: 4) {
                        Text("🔥")
                            .font(.system(size: 18))
                        Text("\(store.getStreak())")
                            .font(.system(size: 20, weight: .bold, design: .rounded))
                            .foregroundColor(.orange)
                        Text("streak_days")
                            .font(.system(size: 14, weight: .medium, design: .rounded))
                            .foregroundColor(.gray)
                    }
                    .padding(.top, 4)
                }

                Spacer()
            }

            // Splash droplets (behind toilet)
            if showSplash {
                ForEach(splashDroplets) { droplet in
                    Circle()
                        .fill(Color.blue.opacity(0.5))
                        .frame(width: droplet.size, height: droplet.size)
                        .offset(
                            x: cos(droplet.angle) * droplet.distance * splashProgress,
                            y: toiletY + sin(droplet.angle) * droplet.distance * splashProgress - 20
                        )
                        .opacity(Double(1.0 - splashProgress))
                }
            }

            // Poop emoji
            Text("💩")
                .font(.system(size: 80))
                .rotationEffect(.degrees(poopRotation))
                .scaleEffect(poopScale)
                .opacity(poopOpacity)
                .offset(y: poopY)
                .zIndex(poopY < toiletY ? 2 : 0)

            // Toilet button
            Button(action: {
                if !isAnimating {
                    logPoop()
                }
            }) {
                Text("🚽")
                    .font(.system(size: 150))
                    .scaleEffect(toiletScale)
                    .offset(x: toiletShake, y: toiletY)
            }
            .zIndex(1)

            // "Tap me" hint
            if todayCount == 0 && !isAnimating {
                Text("Tap the toilet!")
                    .font(.system(size: 16, weight: .medium, design: .rounded))
                    .foregroundColor(.gray.opacity(0.6))
                    .offset(y: toiletY + 120)
            }

            // Banner ad at bottom
            VStack {
                Spacer()
                BannerAdView(adUnitID: AdConfig.bannerAdUnitID)
                    .frame(height: 50)
                    .padding(.bottom, 16)
            }
        }
    }

    func logPoop() {
        isAnimating = true

        // Reset states instantly (no animation)
        poopY = -500
        poopRotation = Double.random(in: -15...15)
        poopScale = 1.0
        poopOpacity = 1.0
        showSplash = false
        splashProgress = 0
        toiletShake = 0
        toiletScale = 1.0

        // Generate random splash droplets
        splashDroplets = (0..<8).map { _ in
            SplashDroplet(
                angle: Double.random(in: -Double.pi...(-0.2)),
                distance: CGFloat.random(in: 30...80),
                size: CGFloat.random(in: 4...10)
            )
        }

        // Small delay so SwiftUI renders the reset position first
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.05) {
            // Phase 1: Poop falls from top of screen with rotation
            withAnimation(.easeIn(duration: 0.7)) {
                poopY = toiletY - 30
                poopRotation += Double.random(in: -360...360)
            }
        }

        // Phase 2: Poop shrinks into toilet
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.7) {
            withAnimation(.easeIn(duration: 0.2)) {
                poopScale = 0.3
                poopOpacity = 0.0
                poopY = toiletY + 10
            }
        }

        // Phase 3: Toilet impact - shake + squish
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.85) {
            store.addLog()

            // Plop sound
            if let url = Bundle.main.url(forResource: "plop", withExtension: "wav") {
                audioPlayer = try? AVAudioPlayer(contentsOf: url)
                audioPlayer?.play()
            }

            // Haptic feedback
            let impact = UIImpactFeedbackGenerator(style: .medium)
            impact.impactOccurred()

            // Toilet squish
            withAnimation(.spring(response: 0.2, dampingFraction: 0.3)) {
                toiletScale = 1.12
            }
            DispatchQueue.main.asyncAfter(deadline: .now() + 0.15) {
                withAnimation(.spring(response: 0.3, dampingFraction: 0.4)) {
                    toiletScale = 1.0
                }
            }

            // Toilet shake
            shakeToilet()

            // Splash
            showSplash = true
            withAnimation(.easeOut(duration: 0.5)) {
                splashProgress = 1.0
            }

            // Counter pop
            withAnimation(.spring(response: 0.3, dampingFraction: 0.4)) {
                counterScale = 1.3
            }
            DispatchQueue.main.asyncAfter(deadline: .now() + 0.2) {
                withAnimation(.spring(response: 0.3, dampingFraction: 0.5)) {
                    counterScale = 1.0
                }
            }
        }

        // Cleanup + show interstitial ad
        DispatchQueue.main.asyncAfter(deadline: .now() + 1.5) {
            showSplash = false
            isAnimating = false
            interstitialAd.showAd()
        }
    }

    func shakeToilet() {
        let shakeSequence: [(CGFloat, Double)] = [
            (8, 0.04), (-8, 0.08), (6, 0.12), (-6, 0.16),
            (4, 0.20), (-3, 0.24), (2, 0.28), (0, 0.32)
        ]

        for (offset, delay) in shakeSequence {
            DispatchQueue.main.asyncAfter(deadline: .now() + delay) {
                withAnimation(.linear(duration: 0.04)) {
                    toiletShake = offset
                }
            }
        }
    }
}

