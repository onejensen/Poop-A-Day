import SwiftUI
import Combine

struct PoopLog: Codable, Identifiable {
    var id: UUID = UUID()
    var timestamp: Date
}

class DataStore: ObservableObject {
    @Published var logs: [PoopLog] = []

    private let fileName = "poop_logs.json"

    init() {
        loadLogs()
    }

    private var fileURL: URL {
        let documents = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)[0]
        return documents.appendingPathComponent(fileName)
    }

    func addLog() {
        let newLog = PoopLog(timestamp: Date())
        logs.append(newLog)
        saveLogs()
    }

    private func saveLogs() {
        do {
            let data = try JSONEncoder().encode(logs)
            try data.write(to: fileURL)
        } catch {
            print("Error saving logs: \(error)")
        }
    }

    private func loadLogs() {
        guard FileManager.default.fileExists(atPath: fileURL.path) else {
            logs = []
            return
        }
        do {
            let data = try Data(contentsOf: fileURL)
            logs = try JSONDecoder().decode([PoopLog].self, from: data)
        } catch {
            print("Error loading logs: \(error)")
            logs = []
        }
    }

    func getStats() -> (today: Int, week: Int, month: Int, year: Int) {
        let now = Date()
        let calendar = Calendar.current

        var todayCount = 0
        var weekCount = 0
        var monthCount = 0
        var yearCount = 0

        for log in logs {
            if calendar.isDate(log.timestamp, equalTo: now, toGranularity: .year) {
                yearCount += 1
                if calendar.isDate(log.timestamp, equalTo: now, toGranularity: .month) {
                    monthCount += 1
                    if calendar.isDate(log.timestamp, equalTo: now, toGranularity: .weekOfYear) {
                        weekCount += 1
                        if calendar.isDate(log.timestamp, inSameDayAs: now) {
                            todayCount += 1
                        }
                    }
                }
            }
        }

        return (todayCount, weekCount, monthCount, yearCount)
    }

    func deleteLog(_ log: PoopLog) {
        logs.removeAll { $0.id == log.id }
        saveLogs()
    }

    func deleteAllLogs() {
        logs.removeAll()
        saveLogs()
    }

    // MARK: - Streak

    func getStreak() -> Int {
        let calendar = Calendar.current
        let today = calendar.startOfDay(for: Date())

        // Get unique days with logs, sorted descending
        let uniqueDays = Set(logs.map { calendar.startOfDay(for: $0.timestamp) })
            .sorted(by: >)

        guard !uniqueDays.isEmpty else { return 0 }

        // Check if today or yesterday has a log (streak must be current)
        let yesterday = calendar.date(byAdding: .day, value: -1, to: today)!
        guard uniqueDays.first == today || uniqueDays.first == yesterday else { return 0 }

        var streak = 0
        var expectedDay = uniqueDays.first!

        for day in uniqueDays {
            if day == expectedDay {
                streak += 1
                expectedDay = calendar.date(byAdding: .day, value: -1, to: expectedDay)!
            } else if day < expectedDay {
                break
            }
        }

        return streak
    }

    // MARK: - Export / Import

    func exportData() -> Data? {
        try? JSONEncoder().encode(logs)
    }

    func exportFileURL() -> URL? {
        let tempDir = FileManager.default.temporaryDirectory
        let exportURL = tempDir.appendingPathComponent("poop_a_day_backup.json")
        guard let data = exportData() else { return nil }
        do {
            try data.write(to: exportURL)
            return exportURL
        } catch {
            return nil
        }
    }

    func importData(from url: URL) -> Bool {
        do {
            let data = try Data(contentsOf: url)
            let importedLogs = try JSONDecoder().decode([PoopLog].self, from: data)
            logs = importedLogs
            saveLogs()
            return true
        } catch {
            print("Error importing data: \(error)")
            return false
        }
    }
}
