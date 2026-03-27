import SwiftUI

struct StatsView: View {
    @ObservedObject var store: DataStore
    
    var body: some View {
        let stats = store.getStats()

        NavigationView {
            ZStack {
                TileBackground()
                    .ignoresSafeArea()

                ScrollView {
                    VStack(spacing: 20) {
                        StatCard(label: "Today", count: stats.today)
                        StatCard(label: "This Week", count: stats.week)
                        StatCard(label: "This Month", count: stats.month)
                        StatCard(label: "This Year", count: stats.year)
                    }
                    .padding()
                }
            }
            .navigationTitle("Statistics 📊")
        }
    }
}

struct StatCard: View {
    let label: LocalizedStringKey
    let count: Int
    
    var body: some View {
        HStack {
            Text(label)
                .font(.headline)
            Spacer()
            Text("\(count)")
                .font(.system(size: 24, weight: .bold))
                .foregroundColor(.green)
        }
        .padding()
        .background(Color(.secondarySystemBackground))
        .cornerRadius(12)
        .shadow(radius: 2)
    }
}
