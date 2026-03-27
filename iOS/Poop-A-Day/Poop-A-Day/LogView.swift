import SwiftUI

struct LogView: View {
    @ObservedObject var store: DataStore
    @State private var showDeleteAllAlert = false
    @State private var selectedLogs = Set<UUID>()
    @State private var editMode: EditMode = .inactive

    var sortedLogs: [PoopLog] {
        store.logs.sorted(by: { $0.timestamp > $1.timestamp })
    }

    var body: some View {
        NavigationView {
            ZStack {
                TileBackground()
                    .ignoresSafeArea()

                List(selection: $selectedLogs) {
                    ForEach(sortedLogs) { log in
                        HStack {
                            Text(log.timestamp, style: .date)
                                .foregroundColor(.primary)
                            Spacer()
                            Text(log.timestamp, style: .time)
                                .foregroundColor(.secondary)
                        }
                        .listRowBackground(Color(.secondarySystemBackground).opacity(0.9))
                    }
                }
                .scrollContentBackground(.hidden)
                .environment(\.editMode, $editMode)
            }
            .navigationTitle("History 💩")
            .toolbar {
                ToolbarItem(placement: .topBarLeading) {
                    if !store.logs.isEmpty {
                        if editMode == .active && !selectedLogs.isEmpty {
                            Button(role: .destructive) {
                                for id in selectedLogs {
                                    if let log = store.logs.first(where: { $0.id == id }) {
                                        store.deleteLog(log)
                                    }
                                }
                                selectedLogs.removeAll()
                                editMode = .inactive
                            } label: {
                                Text("delete_selected")
                                    .foregroundColor(.red)
                            }
                        } else {
                            Button(role: .destructive) {
                                showDeleteAllAlert = true
                            } label: {
                                Image(systemName: "trash")
                                    .foregroundColor(.red)
                            }
                        }
                    }
                }
                ToolbarItem(placement: .topBarTrailing) {
                    if !store.logs.isEmpty {
                        Button {
                            withAnimation {
                                if editMode == .active {
                                    editMode = .inactive
                                    selectedLogs.removeAll()
                                } else {
                                    editMode = .active
                                }
                            }
                        } label: {
                            Text(editMode == .active ? "done" : "edit")
                        }
                    }
                }
            }
            .alert("delete_all_title", isPresented: $showDeleteAllAlert) {
                Button("cancel", role: .cancel) {}
                Button("delete_all_confirm", role: .destructive) {
                    store.deleteAllLogs()
                }
            } message: {
                Text("delete_all_message")
            }
        }
    }
}
