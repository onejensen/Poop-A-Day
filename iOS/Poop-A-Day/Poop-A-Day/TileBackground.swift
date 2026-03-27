import SwiftUI

struct TileBackground: View {
    @Environment(\.colorScheme) var colorScheme

    let tileSize: CGFloat = 60
    let groutWidth: CGFloat = 2

    var tileColor1: Color {
        colorScheme == .dark
            ? Color(red: 0.18, green: 0.20, blue: 0.22)
            : Color(red: 0.92, green: 0.94, blue: 0.96)
    }

    var tileColor2: Color {
        colorScheme == .dark
            ? Color(red: 0.14, green: 0.16, blue: 0.18)
            : Color(red: 0.86, green: 0.89, blue: 0.92)
    }

    var groutColor: Color {
        colorScheme == .dark
            ? Color(red: 0.08, green: 0.09, blue: 0.10)
            : Color(red: 0.78, green: 0.80, blue: 0.82)
    }

    var body: some View {
        Canvas { context, size in
            context.fill(
                Path(CGRect(origin: .zero, size: size)),
                with: .color(groutColor)
            )

            let cols = Int(size.width / tileSize) + 1
            let rows = Int(size.height / tileSize) + 1

            for row in 0..<rows {
                for col in 0..<cols {
                    let isAlternate = (row + col) % 2 == 0
                    let color = isAlternate ? tileColor1 : tileColor2

                    let x = CGFloat(col) * tileSize + groutWidth / 2
                    let y = CGFloat(row) * tileSize + groutWidth / 2
                    let rect = CGRect(
                        x: x,
                        y: y,
                        width: tileSize - groutWidth,
                        height: tileSize - groutWidth
                    )

                    let path = Path(roundedRect: rect, cornerRadius: 1)
                    context.fill(path, with: .color(color))
                }
            }
        }
    }
}
