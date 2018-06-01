package eloc.contract

import eloc.state.LetterOfCreditApplicationState
import eloc.state.LetterOfCreditApplicationStatus
import net.corda.core.contracts.*
import net.corda.core.transactions.LedgerTransaction

/**
 * Letter of Credit Application
 */
class LetterOfCreditApplicationContract : Contract {
    companion object {
        @JvmStatic
        val CONTRACT_ID = "eloc.contract.LetterOfCreditApplicationContract"
    }

    interface Commands : CommandData {
        class Apply : TypeOnlyCommandData(), Commands
        class Approve : TypeOnlyCommandData(), Commands
    }

    override fun verify(tx: LedgerTransaction) {
        val command = tx.commands.requireSingleCommand<Commands>()

        when (command.value) {
            is Commands.Apply -> requireThat {
                val output = tx.outputsOfType<LetterOfCreditApplicationState>().single()
                "the owner must be the applicant" using (output.owner == output.props.applicant)
                "there is no input state" using tx.inputStates.isEmpty()
                "the output status must be pending issuer review" using (output.status == LetterOfCreditApplicationStatus.IN_REVIEW)
            }
            is Commands.Approve -> {
                // TODO: Add approval logic.
            }
        }
    }
}