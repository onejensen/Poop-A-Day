import SwiftUI

struct SplashView: View {
    @State private var poopRotation: Double = 0
    @State private var titleScale: CGFloat = 0.3
    @State private var titleOpacity: Double = 0
    @State private var poopOpacity: Double = 0

    var body: some View {
        ZStack {
            TileBackground()
                .ignoresSafeArea()

            VStack(spacing: 8) {
                // Title
                VStack(spacing: -8) {
                    Text("POOP")
                        .font(.system(size: 72, weight: .black, design: .rounded))
                    Text("A")
                        .font(.system(size: 52, weight: .black, design: .rounded))
                    Text("DAY")
                        .font(.system(size: 72, weight: .black, design: .rounded))
                }
                .foregroundColor(.brown)
                .shadow(color: .black.opacity(0.3), radius: 4, x: 2, y: 2)
                .scaleEffect(titleScale)
                .opacity(titleOpacity)

                Spacer().frame(height: 40)

                // Spinning poop
                Text("💩")
                    .font(.system(size: 60))
                    .rotationEffect(.degrees(poopRotation))
                    .opacity(poopOpacity)
            }
        }
        .onAppear {
            // Title entrance
            withAnimation(.spring(response: 0.5, dampingFraction: 0.6)) {
                titleScale = 1.0
                titleOpacity = 1.0
            }

            // Poop fade in
            withAnimation(.easeIn(duration: 0.3).delay(0.3)) {
                poopOpacity = 1.0
            }

            // Poop spin
            withAnimation(.linear(duration: 1.0).repeatForever(autoreverses: false)) {
                poopRotation = 360
            }
        }
    }
}
