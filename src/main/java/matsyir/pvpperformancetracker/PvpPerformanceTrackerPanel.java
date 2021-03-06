/*
 * Copyright (c)  2020, Matsyir <https://github.com/Matsyir>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package matsyir.pvpperformancetracker;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.inject.Inject;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;

class PvpPerformanceTrackerPanel extends PluginPanel
{
	// The main fight history container, this will hold all the individual FightPerformancePanels.
	private final JPanel fightHistoryContainer = new JPanel();

	private final TotalStatsPanel totalStatsPanel = new TotalStatsPanel();
	private final JPopupMenu popupMenu = new JPopupMenu();

	private final PvpPerformanceTrackerPlugin plugin;
	private final PvpPerformanceTrackerConfig config;

	@Inject
	private PvpPerformanceTrackerPanel(final PvpPerformanceTrackerPlugin plugin, final PvpPerformanceTrackerConfig config)
	{
		super(false);
		this.plugin = plugin;
		this.config = config;

		setLayout(new BorderLayout(0, 4));
		setBackground(ColorScheme.DARK_GRAY_COLOR);
		setBorder(new EmptyBorder(8, 8, 8, 8));
		JPanel mainContent = new JPanel(new BorderLayout());

		fightHistoryContainer.setSize(getSize());
		fightHistoryContainer.setLayout(new BoxLayout(fightHistoryContainer, BoxLayout.Y_AXIS));

		add(totalStatsPanel, BorderLayout.NORTH, 0);

		// wrap mainContent with scrollpane so it has a scrollbar
		JScrollPane scrollableContainer = new JScrollPane(mainContent);
		scrollableContainer.setBackground(ColorScheme.DARK_GRAY_COLOR);
		scrollableContainer.getVerticalScrollBar().setPreferredSize(new Dimension(6, 0));

		// initialize context menu
		popupMenu.setBorder(new EmptyBorder(5, 5, 5, 5));
		// Create "Reset All" popup menu item
		final JMenuItem reset = new JMenuItem("Reset All");
		reset.addActionListener(e ->
		{
			totalStatsPanel.reset();
			fightHistoryContainer.removeAll();
			SwingUtilities.invokeLater(this::updateUI);
			plugin.resetFightHistory();
		});
		popupMenu.add(reset);

		setComponentPopupMenu(popupMenu);
		mainContent.setComponentPopupMenu(popupMenu);

		mainContent.add(fightHistoryContainer, BorderLayout.NORTH);
		add(scrollableContainer, BorderLayout.CENTER);
	}

	public void addFight(FightPerformance fight)
	{
		totalStatsPanel.addFight(fight);

		SwingUtilities.invokeLater(() ->
		{
			FightPerformancePanel panel = new FightPerformancePanel(fight);
			panel.setComponentPopupMenu(popupMenu);
			panel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(4, 0, 0, 0, ColorScheme.DARK_GRAY_COLOR),
				BorderFactory.createEmptyBorder(8, 8, 2, 8)  // bottom is 2 due to extra spacing coming from somewhere else.
			));

			fightHistoryContainer.add(panel, 0);
			updateUI();
		});
	}

	public void addFights(FightPerformance[] fights)
	{

		totalStatsPanel.addFights(fights);
		SwingUtilities.invokeLater(() ->
		{
			for (FightPerformance fight : fights)
			{
				FightPerformancePanel panel = new FightPerformancePanel(fight);
				panel.setComponentPopupMenu(popupMenu);
				panel.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createMatteBorder(4, 0, 0, 0, ColorScheme.DARK_GRAY_COLOR),
					BorderFactory.createEmptyBorder(8, 8, 2, 8)  // bottom is 2 due to extra spacing coming from somewhere else.
				));

				fightHistoryContainer.add(panel, 0);
			}

			updateUI();
		});
	}

	public void rebuild()
	{
		totalStatsPanel.reset();
		fightHistoryContainer.removeAll();
		if (plugin.fightHistory.size() > 0)
		{
			addFights(plugin.fightHistory.toArray(new FightPerformance[0]));
		}
	}
}