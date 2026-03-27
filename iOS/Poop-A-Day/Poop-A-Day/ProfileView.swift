import SwiftUI
import UniformTypeIdentifiers

struct ProfileView: View {
    @ObservedObject var store: DataStore
    @Binding var isDarkMode: Bool

    @State private var showImportPicker = false
    @State private var showImportSuccess = false
    @State private var showImportError = false

    var appVersion: String {
        let version = Bundle.main.infoDictionary?["CFBundleShortVersionString"] as? String ?? "1.0"
        let build = Bundle.main.infoDictionary?["CFBundleVersion"] as? String ?? "1"
        return "\(version) (\(build))"
    }

    var body: some View {
        NavigationView {
            ZStack {
                TileBackground()
                    .ignoresSafeArea()

                ScrollView {
                    VStack(spacing: 20) {

                        // Stats summary card
                        VStack(spacing: 12) {
                            Text("💩")
                                .font(.system(size: 60))

                            let streak = store.getStreak()

                            Text("\(store.logs.count)")
                                .font(.system(size: 48, weight: .black, design: .rounded))
                                .foregroundColor(.brown)

                            Text("total_logs")
                                .font(.system(size: 14, weight: .medium, design: .rounded))
                                .foregroundColor(.gray)

                            if streak > 1 {
                                HStack(spacing: 4) {
                                    Text("🔥")
                                    Text("\(streak)")
                                        .font(.system(size: 20, weight: .bold, design: .rounded))
                                        .foregroundColor(.orange)
                                    Text("streak_days")
                                        .font(.system(size: 14, design: .rounded))
                                        .foregroundColor(.gray)
                                }
                            }
                        }
                        .padding(24)
                        .frame(maxWidth: .infinity)
                        .background(Color(.secondarySystemBackground).opacity(0.9))
                        .cornerRadius(16)

                        // Theme
                        VStack(spacing: 0) {
                            ProfileRow(icon: isDarkMode ? "moon.fill" : "sun.max.fill",
                                       iconColor: isDarkMode ? .yellow : .orange,
                                       title: "theme") {
                                Toggle("", isOn: Binding(
                                    get: { isDarkMode },
                                    set: { val in
                                        withAnimation(.easeInOut(duration: 0.3)) {
                                            isDarkMode = val
                                        }
                                    }
                                ))
                                .labelsHidden()
                            }
                        }
                        .background(Color(.secondarySystemBackground).opacity(0.9))
                        .cornerRadius(12)

                        // Data management
                        VStack(spacing: 0) {
                            // Export
                            if let exportURL = store.exportFileURL() {
                                ShareLink(item: exportURL) {
                                    ProfileRow(icon: "square.and.arrow.up", iconColor: .blue, title: "export_data") {
                                        Image(systemName: "chevron.right")
                                            .foregroundColor(.gray)
                                    }
                                }
                                .buttonStyle(.plain)
                            }

                            Divider().padding(.leading, 52)

                            // Import
                            Button {
                                showImportPicker = true
                            } label: {
                                ProfileRow(icon: "square.and.arrow.down", iconColor: .green, title: "import_data") {
                                    Image(systemName: "chevron.right")
                                        .foregroundColor(.gray)
                                }
                            }
                            .buttonStyle(.plain)
                        }
                        .background(Color(.secondarySystemBackground).opacity(0.9))
                        .cornerRadius(12)

                        // Other apps
                        VStack(spacing: 0) {
                            Link(destination: URL(string: "https://apps.apple.com/app/3dcalc/id6752701677")!) {
                                ProfileRow(icon: "cube", iconColor: .purple, title: "other_apps") {
                                    VStack(alignment: .trailing) {
                                        Text("3Dcalc+")
                                            .font(.system(size: 14, weight: .medium))
                                            .foregroundColor(.purple)
                                        Text("other_apps_subtitle")
                                            .font(.system(size: 11))
                                            .foregroundColor(.gray)
                                    }
                                }
                            }
                            .buttonStyle(.plain)
                        }
                        .background(Color(.secondarySystemBackground).opacity(0.9))
                        .cornerRadius(12)

                        // App info
                        VStack(spacing: 0) {
                            ProfileRow(icon: "info.circle", iconColor: .gray, title: "version") {
                                Text(appVersion)
                                    .foregroundColor(.gray)
                            }
                        }
                        .background(Color(.secondarySystemBackground).opacity(0.9))
                        .cornerRadius(12)
                    }
                    .padding()
                }
            }
            .navigationTitle("Profile")
            .fileImporter(isPresented: $showImportPicker,
                          allowedContentTypes: [.json]) { result in
                switch result {
                case .success(let url):
                    if url.startAccessingSecurityScopedResource() {
                        let success = store.importData(from: url)
                        url.stopAccessingSecurityScopedResource()
                        if success {
                            showImportSuccess = true
                        } else {
                            showImportError = true
                        }
                    }
                case .failure:
                    showImportError = true
                }
            }
            .alert("import_success", isPresented: $showImportSuccess) {
                Button("OK") {}
            }
            .alert("import_error", isPresented: $showImportError) {
                Button("OK") {}
            }
        }
    }
}

// MARK: - Profile Row

struct ProfileRow<Trailing: View>: View {
    let icon: String
    let iconColor: Color
    let title: LocalizedStringKey
    @ViewBuilder let trailing: Trailing

    var body: some View {
        HStack(spacing: 12) {
            Image(systemName: icon)
                .font(.system(size: 18))
                .foregroundColor(iconColor)
                .frame(width: 28)
            Text(title)
                .font(.system(size: 16))
            Spacer()
            trailing
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 14)
    }
}
