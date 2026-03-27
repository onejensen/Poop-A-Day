import SwiftUI

struct ContentView: View {
    @StateObject private var store = DataStore()
    @AppStorage("isDarkMode") private var isDarkMode = false
    @State private var showSplash = true

    var body: some View {
        ZStack {
            TabView {
                TrackerView(store: store, isDarkMode: $isDarkMode)
                    .tabItem {
                        Label("Tracker", systemImage: "circle.circle")
                    }

                LogView(store: store)
                    .tabItem {
                        Label("Log", systemImage: "list.bullet")
                    }

                StatsView(store: store)
                    .tabItem {
                        Label("Stats", systemImage: "chart.bar")
                    }

                ProfileView(store: store, isDarkMode: $isDarkMode)
                    .tabItem {
                        Label("Profile", systemImage: "person.circle")
                    }
            }

            if showSplash {
                SplashView()
                    .transition(.opacity)
                    .zIndex(1)
            }
        }
        .preferredColorScheme(isDarkMode ? .dark : .light)
        .onAppear {
            DispatchQueue.main.asyncAfter(deadline: .now() + 2.0) {
                withAnimation(.easeOut(duration: 0.4)) {
                    showSplash = false
                }
            }
        }
    }
}
